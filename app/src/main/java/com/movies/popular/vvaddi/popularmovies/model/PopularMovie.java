package com.movies.popular.vvaddi.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.movies.popular.vvaddi.popularmovies.database.MovieEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores Movie Details
 */
public class PopularMovie implements Parcelable {

    private static final String LOG = "PopularMovie";
    private final static String RESULTS = "results";
    private final static String ID = "id";
    private final static String TITLE = "title";
    private final static String IMAGE_PATH = "poster_path";
    private final static String OVERVIEW = "overview";
    private final static String RATING = "vote_average";
    private final static String RELEASE_DATE = "release_date";
    private final static String URL = "url";
    private final static String KEY = "key";

    private String id;
    private String title;
    private String image;
    private String overview;
    private String userRating;
    private String releaseDate;
    private String reviewUrl;
    private ArrayList<String> trailers;

    public PopularMovie() {
    }

    public PopularMovie(MovieEntry movieEntry) {
        this.id = movieEntry.getId();
        this.title = movieEntry.getTitle();
        this.image = movieEntry.getImage();
        this.overview = movieEntry.getOverview();
        this.userRating = movieEntry.getUserRating();
        this.releaseDate = movieEntry.getReleaseDate();
    }

    protected PopularMovie(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.image = in.readString();
        this.overview = in.readString();
        this.userRating = in.readString();
        this.releaseDate = in.readString();
        this.reviewUrl = in.readString();
        this.trailers = in.readArrayList(String.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }

    public ArrayList<String> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<String> trailers) {
        this.trailers = trailers;
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
                    popularMovie.setId(jsonObject.optString(ID));
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

    public static String parseReviewJson(String json) {
        String reviewUrl = new String();
        try {

            JSONObject root = new JSONObject(json);
            JSONArray results = root.getJSONArray(RESULTS);
            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject jsonObject = results.getJSONObject(i);
                    reviewUrl = TextUtils.isEmpty(jsonObject.getString(URL)) ? "" : jsonObject.getString(URL);
                }
            }
        } catch (Exception e) {
            Log.d(LOG, e.getMessage());
        }
        return reviewUrl;
    }

    public static ArrayList<String> parseTrailersJson(String json) {
        ArrayList<String> trailers = new ArrayList<>();
        try {

            JSONObject root = new JSONObject(json);
            JSONArray results = root.getJSONArray(RESULTS);
            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject jsonObject = results.getJSONObject(i);
                    String key = TextUtils.isEmpty(jsonObject.getString(KEY)) ? "" : jsonObject.getString(KEY);
                    trailers.add(key);
                }
            }
        } catch (Exception e) {
            Log.d(LOG, e.getMessage());
        }
        return trailers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(overview);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
        dest.writeString(reviewUrl);
        dest.writeStringList(trailers);
    }

    public static final Parcelable.Creator<PopularMovie> CREATOR = new Parcelable.Creator<PopularMovie>() {
        @Override
        public PopularMovie createFromParcel(Parcel source) {
            return new PopularMovie(source);
        }

        @Override
        public PopularMovie[] newArray(int size) {
            return new PopularMovie[size];
        }
    };
}
