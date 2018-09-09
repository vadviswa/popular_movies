package com.movies.popular.vvaddi.popularmovies.network;

import android.net.Uri;
import android.util.Log;

import com.movies.popular.vvaddi.popularmovies.model.MovieSort;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Class that creates the url and makes the HTTP call.
 * Sample Popular Call
 * https://api.themoviedb.org/3/discover/movie?api_key=<api_key></>&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1
 */

public class MovieDBUrlQuery {

    private static final String LOG = "MovieDBUrlQuery";

    final static String THEMOVIEDB_BASE_URL =
            "https://api.themoviedb.org/3/discover/movie";

    final static String API_KEY = "api_key";
    final static String API_VALUE = "<<API KEY>>";
    final static String LANGUAGE_KEY = "language";
    final static String LANGUAGE_VALUE = "en-US";
    final static String SORY_BY_KEY = "sort_by";
    final static String INCLUDE_ADULT_KEY = "include_adult";
    final static String INCLUDE_ADULT_VALUE = "false";
    final static String INCLUDE_VIDEO_KEY = "include_video";
    final static String INCLUDE_VIDEO_VALUE = "false";
    final static String PAGE_KEY = "page";
    final static String PAGE_VALUE = "1";

    /***
     * @return The URL to use to query the The Movie DB
     */
    public static URL buildPopularMoviesUrl(MovieSort movieSort) {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY, API_VALUE)
                .appendQueryParameter(LANGUAGE_KEY, LANGUAGE_VALUE)
                .appendQueryParameter(SORY_BY_KEY, movieSort.getSortParameter())
                .appendQueryParameter(INCLUDE_ADULT_KEY, INCLUDE_ADULT_VALUE)
                .appendQueryParameter(INCLUDE_VIDEO_KEY, INCLUDE_VIDEO_VALUE)
                .appendQueryParameter(PAGE_KEY, PAGE_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.d(LOG, url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
