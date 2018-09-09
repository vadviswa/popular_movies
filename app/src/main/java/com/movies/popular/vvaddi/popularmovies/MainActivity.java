package com.movies.popular.vvaddi.popularmovies;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.movies.popular.vvaddi.popularmovies.model.MovieSort;
import com.movies.popular.vvaddi.popularmovies.model.PopularMovie;
import com.movies.popular.vvaddi.popularmovies.network.MovieDBUrlQuery;

import java.io.IOException;
import java.net.URL;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        adapter = new MovieArrayAdapter(this, new ArrayList<PopularMovie>());
        gridView.setAdapter(adapter);
        createPopularMoviesQuery(MovieSort.MOST_POPULAR);
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
        URL popularMoviesUrl = MovieDBUrlQuery.buildPopularMoviesUrl(movieSort);
        new MovieDBAsyncTask().execute(popularMoviesUrl);
    }

    public class MovieDBAsyncTask extends AsyncTask<URL, Void, List<PopularMovie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        @Nullable
        protected List<PopularMovie> doInBackground(URL... params) {
            URL searchUrl = params[0];
            List<PopularMovie> movieList = null;
            try {
                String responseFromHttpUrl = MovieDBUrlQuery.getResponseFromHttpUrl(searchUrl);
                movieList = PopularMovie.parseJson(responseFromHttpUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(List<PopularMovie> movieList) {
            progressBar.setVisibility(View.INVISIBLE);
            if (movieList != null && !movieList.isEmpty()) {
                adapter.clear();
                adapter.addAll(movieList);
                adapter.notifyDataSetChanged();
            } else {
                // COMPLETED (16) Call showErrorMessage if the result is null in onPostExecute
                //showErrorMessage();
            }
        }
    }
}
