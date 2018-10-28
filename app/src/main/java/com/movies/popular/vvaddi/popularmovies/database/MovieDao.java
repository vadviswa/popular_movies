package com.movies.popular.vvaddi.popularmovies.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by vvaddi on 10/27/18.
 */
@Dao
public interface MovieDao {
    @Query("SELECT * FROM movie ORDER BY updated_at desc")
    List<MovieEntry> loadAllMovies();

    @Insert
    void insertTask(MovieEntry taskEntry);

    @Delete
    void deleteTask(MovieEntry taskEntry);

    @Query("SELECT * FROM movie WHERE id = :id")
    MovieEntry findMovieById(String id);
}
