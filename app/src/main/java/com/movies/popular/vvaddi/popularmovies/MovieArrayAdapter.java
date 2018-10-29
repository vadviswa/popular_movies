package com.movies.popular.vvaddi.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.movies.popular.vvaddi.popularmovies.model.PopularMovie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Adapter for movies.
 */
public class MovieArrayAdapter extends RecyclerView.Adapter<MovieArrayAdapter.PopularMovieViewHolder> {

    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private ArrayList<PopularMovie> popularMovies;
    private Context context;
    private final ListItemClickListener listener;

    public interface ListItemClickListener {
        void onItemClick(int index);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MovieArrayAdapter(@NonNull Context context, @NonNull ArrayList<PopularMovie> objects, @NonNull ListItemClickListener listener) {
        this.context = context;
        this.popularMovies = objects;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MovieArrayAdapter.PopularMovieViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.movie_poster_item, parent, false);
        return new PopularMovieViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PopularMovieViewHolder holder, int position) {
        final PopularMovie movie = popularMovies.get(position);

        Picasso.with(context).load(IMAGE_BASE_URL + movie.getImage()).into(holder.imageView);
        holder.imageView.setContentDescription(movie.getTitle());
    }


    public void refreshPopularMovies(List<PopularMovie> items) {
        popularMovies.clear();
        popularMovies.addAll(items);
        notifyDataSetChanged();
    }

    public PopularMovie getMovie(int i) {
        return popularMovies.get(i);
    }

    /**
     * @return Return the size of your dataset (invoked by the layout manager)
     */
    @Override
    public int getItemCount() {
        return popularMovies.size();
    }

    public ArrayList<PopularMovie> getPopularMovies() {
        return popularMovies;
    }

    /**
     *
     */
    public class PopularMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;

        public PopularMovieViewHolder(View view) {
            super(view);
            imageView = itemView.findViewById(R.id.movie_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            listener.onItemClick(clickedPosition);
        }
    }
}
