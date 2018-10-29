package com.movies.popular.vvaddi.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movies.popular.vvaddi.popularmovies.database.MainViewModel;
import com.movies.popular.vvaddi.popularmovies.database.MovieEntry;
import com.movies.popular.vvaddi.popularmovies.model.MovieSort;
import com.movies.popular.vvaddi.popularmovies.model.PopularMovie;
import com.movies.popular.vvaddi.popularmovies.network.MovieDBUrlQuery;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieArrayAdapter.ListItemClickListener {

    private final String RECYCLER_VIEW_POSITION = "recycler_position";
    private final String RECYCLER_VIEW_DATASET = "recycler_data";
    private final String LAST_SORT_ORDER = "last_sort_order";
    private GridLayoutManager gridLayoutManager;
    private int savedSortOption;

    private MovieArrayAdapter adapter;
    @BindView(R.id.movie_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.loading_indicator)
    ProgressBar progressBar;
    @BindView(R.id.error_textview)
    TextView errorTextView;

    // Instantiate the RequestQueue.
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        ButterKnife.bind(this);
        gridLayoutManager = new GridLayoutManager(this, calculateNoOfColumns());
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new MovieArrayAdapter(this, new ArrayList<PopularMovie>(), this);
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(LAST_SORT_ORDER))
            savedSortOption = savedInstanceState.getInt(LAST_SORT_ORDER);
        else
            savedSortOption = -1;

        if (savedInstanceState != null && savedInstanceState.containsKey(RECYCLER_VIEW_DATASET)) {
            restorePreviousState(savedInstanceState);
        } else {
            createPopularMoviesQuery(MovieSort.MOST_POPULAR);
        }
    }

    public void restorePreviousState(Bundle savedInstanceState) {
        // getting recyclerview position
        Parcelable mListState = savedInstanceState.getParcelable(RECYCLER_VIEW_POSITION);
        // getting recyclerview items
        ArrayList<PopularMovie> mDataset = savedInstanceState.getParcelableArrayList(RECYCLER_VIEW_DATASET);
        // Restoring adapter items
        adapter.refreshPopularMovies(mDataset);
        // Restoring recycler view position
        recyclerView.getLayoutManager().onRestoreInstanceState(mListState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        // save recyclerview position
        outState.putParcelable(RECYCLER_VIEW_POSITION, listState);
        // save recyclerview items
        outState.putParcelableArrayList(RECYCLER_VIEW_DATASET, adapter.getPopularMovies());
        // save sort option
        outState.putInt(LAST_SORT_ORDER, savedSortOption);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            savedSortOption = savedInstanceState.getInt(LAST_SORT_ORDER);
        }
    }

    public int calculateNoOfColumns() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if (noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        if (savedSortOption != -1) {
            MenuItem selected = menu.findItem(savedSortOption);
            selected.setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(false);
        savedSortOption = item.getItemId();
        switch (item.getItemId()) {
            case R.id.movie_sort_most_popular:
                createPopularMoviesQuery(MovieSort.MOST_POPULAR);
                recyclerView.getLayoutManager().scrollToPosition(0);
                return true;

            case R.id.movie_sort_top_rated:
                createPopularMoviesQuery(MovieSort.TOP_RATED);
                recyclerView.getLayoutManager().scrollToPosition(0);
                return true;

            case R.id.movie_favorite:
                loadFavoritesFromDB();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createPopularMoviesQuery(MovieSort movieSort) {
        final String popularMoviesUrl = MovieDBUrlQuery.buildPopularMoviesUrl(movieSort);

        progressBar.setVisibility(View.VISIBLE);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, popularMoviesUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String moviesResponse) {
                        final List<PopularMovie> movieList = PopularMovie.parseJson(moviesResponse);
                        final Hashtable<String, PopularMovie> movieMap = new Hashtable<>();
                        progressBar.setVisibility(View.INVISIBLE);
                        errorTextView.setVisibility(View.GONE);
                        if (movieList != null && !movieList.isEmpty()) {
                            adapter.refreshPopularMovies(movieList);
                        } else {
                            errorTextView.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void loadFavoritesFromDB() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getMovieEntries().observe(MainActivity.this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> dbMovieEntries) {
                final ArrayList<PopularMovie> movies = new ArrayList<>();
                for (MovieEntry entry : dbMovieEntries) {
                    movies.add(new PopularMovie(entry));
                }
                adapter.refreshPopularMovies(movies);
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        });
    }

    @Override
    public void onItemClick(final int index) {

        final PopularMovie movie = adapter.getMovie(index);
        final String reviewUrl = MovieDBUrlQuery.buildReviewUrl(movie.getId());
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, reviewUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String url = PopularMovie.parseReviewJson(response);
                        movie.setReviewUrl(url);
                        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailActivity.MOVIE, adapter.getMovie(index));
                        MainActivity.this.startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

}
