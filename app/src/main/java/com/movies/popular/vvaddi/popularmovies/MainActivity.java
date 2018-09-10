package com.movies.popular.vvaddi.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movies.popular.vvaddi.popularmovies.model.MovieSort;
import com.movies.popular.vvaddi.popularmovies.model.PopularMovie;
import com.movies.popular.vvaddi.popularmovies.network.MovieDBUrlQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private MovieArrayAdapter adapter;
    @BindView(R.id.movie_grid_view)
    GridView gridView;
    @BindView(R.id.loading_indicator)
    ProgressBar progressBar;
    @BindView(R.id.error_textview)
    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        adapter = new MovieArrayAdapter(this, new ArrayList<PopularMovie>());
        gridView.setAdapter(adapter);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void createPopularMoviesQuery(MovieSort movieSort) {
        final String popularMoviesUrl = MovieDBUrlQuery.buildPopularMoviesUrl(movieSort);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        progressBar.setVisibility(View.VISIBLE);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, popularMoviesUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<PopularMovie> movieList = PopularMovie.parseJson(response);
                        progressBar.setVisibility(View.INVISIBLE);
                        errorTextView.setVisibility(View.GONE);
                        if (movieList != null && !movieList.isEmpty()) {
                            adapter.clear();
                            adapter.addAll(movieList);
                            adapter.notifyDataSetChanged();
                        } else {
                            errorTextView.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
