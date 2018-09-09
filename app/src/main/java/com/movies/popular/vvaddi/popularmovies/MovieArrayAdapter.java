package com.movies.popular.vvaddi.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.movies.popular.vvaddi.popularmovies.model.PopularMovie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Custom Adapter for movies.
 */
public class MovieArrayAdapter extends ArrayAdapter<PopularMovie> {

    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private Context context;

    public MovieArrayAdapter(@NonNull Context context, @NonNull List<PopularMovie> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final PopularMovie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_poster_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.movie_image);
        Picasso.with(getContext()).load(IMAGE_BASE_URL + movie.getImage()).into(imageView);
        imageView.setContentDescription(movie.getTitle());

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.MOVIE, movie);
                context.startActivity(intent);
            }

        });

        return convertView;

    }
}
