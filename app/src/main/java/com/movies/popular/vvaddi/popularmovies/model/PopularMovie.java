package com.movies.popular.vvaddi.popularmovies.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores Movie Details
 */
public class PopularMovie implements Serializable {

    private static final String LOG = "PopularMovie";
    private final static String RESULTS = "results";
    private final static String TITLE = "title";
    private final static String IMAGE_PATH = "poster_path";
    private final static String OVERVIEW = "overview";
    private final static String RATING = "vote_average";
    private final static String RELEASE_DATE = "release_date";

    private String title;
    private String image;
    private String overview;
    private String userRating;
    private String releaseDate;

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


    public static List<PopularMovie> parseJson(String json) {
        List<PopularMovie> movieList = new ArrayList<>();
        try {

            JSONObject root = new JSONObject(json);
            JSONArray results = root.getJSONArray(RESULTS);
            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject jsonObject = results.getJSONObject(i);
                    PopularMovie popularMovie = new PopularMovie();

                    popularMovie.setTitle(jsonObject.optString(TITLE));
                    popularMovie.setUserRating(jsonObject.getString(RATING));
                    popularMovie.setImage(jsonObject.getString(IMAGE_PATH));
                    popularMovie.setOverview(jsonObject.optString(OVERVIEW));
                    popularMovie.setReleaseDate(jsonObject.getString(RELEASE_DATE));
                    movieList.add(popularMovie);
                }
            }
        } catch (Exception e) {
            Log.d(LOG, e.getMessage());
        }
        return movieList;
    }
}
