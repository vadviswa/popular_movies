package com.movies.popular.vvaddi.popularmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 *
 */

@Entity(tableName = "movie")
public class MovieEntry {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String id;
    private String title;
    private String image;
    private String overview;
    private String userRating;
    private String releaseDate;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;


    public MovieEntry(String id, String title, String image, String overview, String userRating, String releaseDate, Date updatedAt) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.updatedAt = updatedAt;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
