package com.movies.popular.vvaddi.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.movies.popular.vvaddi.popularmovies.model.PopularMovie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays the movie details
 */

public class MovieDetailActivity extends AppCompatActivity {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);


        ButterKnife.bind(this);

        PopularMovie movie = (getIntent().getExtras().get(MOVIE) == null) ? null : (PopularMovie) getIntent().getExtras().get(MOVIE);
        if (movie != null) {
            setTitle(R.string.movie_details);

            if (!TextUtils.isEmpty(movie.getTitle()))
                movieTitle.setText(movie.getTitle());

            if (!TextUtils.isEmpty(movie.getUserRating()))
                movieRatings.setText(movie.getUserRating());

            if (!TextUtils.isEmpty(movie.getReleaseDate()) && movie.getReleaseDate().contains("-"))
                movieReleaseDate.setText(movie.getReleaseDate().substring(0, movie.getReleaseDate().indexOf("-")));

            if (!TextUtils.isEmpty(movie.getOverview()))
                movieSnopsis.setText(movie.getOverview());

            if (!TextUtils.isEmpty(movie.getImage()))
                Picasso.with(this).load(MovieArrayAdapter.IMAGE_BASE_URL + movie.getImage()).into(imageView);
        }
    }
}
