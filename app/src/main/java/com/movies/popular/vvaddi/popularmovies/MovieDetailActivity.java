package com.movies.popular.vvaddi.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movies.popular.vvaddi.popularmovies.database.AppDatabase;
import com.movies.popular.vvaddi.popularmovies.database.MovieEntry;
import com.movies.popular.vvaddi.popularmovies.model.PopularMovie;
import com.movies.popular.vvaddi.popularmovies.network.MovieDBUrlQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays the movie details
 */

public class MovieDetailActivity extends AppCompatActivity {

    public static final String VIDEO_ID = "VIDEO_ID";
    public static final String YOUTUBE_BASEURL = "vnd.youtube:";
    public static final String MOVIE = "MOVIE_DATA";

    @BindView(R.id.movie_title_tv)
    TextView movieTitle;

    @BindView(R.id.movie_detail_ratings)
    TextView movieRatings;

    @BindView(R.id.movie_detail_release_date)
    TextView movieReleaseDate;

    @BindView(R.id.movie_synopsis_tv)
    TextView movieSnopsis;

    @BindView(R.id.movie_detail_image)
    ImageView imageView;

    @BindView(R.id.movie_reviews_title)
    TextView reviewTitle;

    @BindView(R.id.movie_reviews_tv)
    TextView reviewURL;

    @BindView(R.id.movie_trailers_title)
    TextView trailersTitle;

    @BindView(R.id.movie_trailer_linear_layout)
    LinearLayout trailerLinearLayout;

    @BindView(R.id.movie_favorite_btn)
    Button favoriteBtn;

    // Instantiate the RequestQueue.
    private RequestQueue queue;

    AppDatabase appDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        queue = Volley.newRequestQueue(this);

        ButterKnife.bind(this);
        appDatabase = AppDatabase.getInstance(this);

        final PopularMovie movie = (getIntent().getExtras().get(MOVIE) == null) ? null : (PopularMovie) getIntent().getExtras().get(MOVIE);
        if (movie != null) {
            setTitle(R.string.movie_details);

            if (!TextUtils.isEmpty(movie.getTitle()))
                movieTitle.setText(movie.getTitle());

            if (!TextUtils.isEmpty(movie.getUserRating()))
                movieRatings.setText(movie.getUserRating());

            if (!TextUtils.isEmpty(movie.getReleaseDate()))
                movieReleaseDate.setText(movie.getReleaseDate());

            if (!TextUtils.isEmpty(movie.getOverview()))
                movieSnopsis.setText(movie.getOverview());

            if (!TextUtils.isEmpty(movie.getImage()))
                Picasso.with(this).load(MovieArrayAdapter.IMAGE_BASE_URL + movie.getImage()).into(imageView);

            setReviewURL(movie);
            setTrailerLinearLayout(movie);
            setFavoriteButton(movie);

        }
    }

    private void setFavoriteButton(final PopularMovie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final MovieEntry existingEntry = appDatabase.taskDao().findMovieById(movie.getId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (existingEntry == null) {
                            favoriteBtn.setText(getResources().getText(R.string.favorite));
                        } else {
                            favoriteBtn.setText(getResources().getText(R.string.unfavorite));
                        }
                    }
                });
            }
        });


        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final MovieEntry existingEntry = appDatabase.taskDao().findMovieById(movie.getId());
                        if (existingEntry != null) {
                            appDatabase.taskDao().deleteTask(existingEntry);
                        } else {
                            MovieEntry movieEntry = new MovieEntry(movie.getId(), movie.getTitle(), movie.getImage(),
                                    movie.getOverview(), movie.getUserRating(), movie.getReleaseDate(), new Date());
                            appDatabase.taskDao().insertTask(movieEntry);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (existingEntry == null) {
                                    favoriteBtn.setText(getResources().getText(R.string.unfavorite));
                                } else {
                                    favoriteBtn.setText(getResources().getText(R.string.favorite));
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void setReviewURL(final PopularMovie movie) {
        if (!TextUtils.isEmpty(movie.getReviewUrl())) {
            reviewURL.setText(movie.getReviewUrl());
            reviewURL.setVisibility(View.VISIBLE);
            reviewTitle.setVisibility(View.VISIBLE);
        }
    }

    private void setTrailerLinearLayout(final PopularMovie movie) {
        final String trailerUrl = MovieDBUrlQuery.buildTrailerUrl(movie.getId());
        // Request a string response from the provided URL.
        StringRequest trailerRequest = new StringRequest(Request.Method.GET, trailerUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final ArrayList<String> trailers = PopularMovie.parseTrailersJson(response);
                        movie.setTrailers(trailers);
                        for (int i = 0; i < trailers.size(); i++) {
                            final String videoId = trailers.get(i);
                            View view = LayoutInflater.from(MovieDetailActivity.this).inflate(R.layout.trailer_item, null, false);
                            TextView trailerId = view.findViewById(R.id.trailer);
                            trailerId.setText(getResources().getText(R.string.trailer).toString() + " " + (i + 1));
                            trailerLinearLayout.addView(view);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    launchYoutube(videoId);
                                }
                            });
                        }
                        if (trailers.size() > 0)
                            trailersTitle.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        // Add the request to the RequestQueue.
        queue.add(trailerRequest);
    }


    public void launchYoutube(String videoId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASEURL + videoId));
        intent.putExtra(VIDEO_ID, videoId);
        this.startActivity(intent);
    }

}
