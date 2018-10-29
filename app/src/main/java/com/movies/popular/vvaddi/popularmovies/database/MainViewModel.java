package com.movies.popular.vvaddi.popularmovies.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.movies.popular.vvaddi.popularmovies.database.AppDatabase;
import com.movies.popular.vvaddi.popularmovies.database.MovieEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<MovieEntry>> movieEntries;

    public MainViewModel(@NonNull Application application) {
        super(application);
        movieEntries = AppDatabase.getInstance(this.getApplication()).taskDao().loadAllMovies();
    }

    public LiveData<List<MovieEntry>> getMovieEntries() {
        return movieEntries;
    }
}
