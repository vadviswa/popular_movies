package com.movies.popular.vvaddi.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.movies.popular.vvaddi.popularmovies.database.AppDatabase;
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

    private MovieArrayAdapter adapter;
    @BindView(R.id.movie_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.loading_indicator)
    ProgressBar progressBar;
    @BindView(R.id.error_textview)
    TextView errorTextView;

    // Instantiate the RequestQueue.
    RequestQueue queue;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);
        appDatabase = AppDatabase.getInstance(this);

        ButterKnife.bind(this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }

        adapter = new MovieArrayAdapter(this, new ArrayList<PopularMovie>(), this);
        recyclerView.setAdapter(adapter);
        createPopularMoviesQuery(MovieSort.MOST_POPULAR);
        errorTextView.setText(R.string.retry_error);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.movie_sort_most_popular:
                createPopularMoviesQuery(MovieSort.MOST_POPULAR);
                return true;

            case R.id.movie_sort_top_rated:
                createPopularMoviesQuery(MovieSort.TOP_RATED);
                return true;

            case R.id.movie_favorite:
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayList<PopularMovie> movies = new ArrayList<>();
                        List<MovieEntry> dbEntries = appDatabase.taskDao().loadAllMovies();
                        for (MovieEntry entry : dbEntries) {
                            movies.add(new PopularMovie(entry));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.refreshPopularMovies(movies);
                            }
                        });
                    }
                });
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
