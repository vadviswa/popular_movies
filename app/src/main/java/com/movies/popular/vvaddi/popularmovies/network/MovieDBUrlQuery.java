package com.movies.popular.vvaddi.popularmovies.network;

import android.net.Uri;

import com.movies.popular.vvaddi.popularmovies.model.MovieSort;

/**
 * Class that creates the url.
 * Sample Popular Call
 * https://api.themoviedb.org/3/discover/movie?api_key=<api_key></>&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1
 */

public class MovieDBUrlQuery {

    private static final String LOG = "MovieDBUrlQuery";

    final static String THEMOVIEDB_BASE_URL =
            "https://api.themoviedb.org/3/movie/";

    final static String API_KEY = "api_key";
    final static String API_VALUE = "<<API KEY>>";
    final static String LANGUAGE_KEY = "language";
    final static String LANGUAGE_VALUE = "en-US";
    final static String INCLUDE_ADULT_KEY = "include_adult";
    final static String INCLUDE_ADULT_VALUE = "false";
    final static String INCLUDE_VIDEO_KEY = "include_video";
    final static String INCLUDE_VIDEO_VALUE = "false";
    final static String PAGE_KEY = "page";
    final static String PAGE_VALUE = "1";

    /***
     * @return The URL to use to query the The Movie DB
     */
    public static String buildPopularMoviesUrl(MovieSort movieSort) {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL + movieSort.getSortParameter()).buildUpon()
                .appendQueryParameter(API_KEY, API_VALUE)
                .appendQueryParameter(LANGUAGE_KEY, LANGUAGE_VALUE)
                .appendQueryParameter(INCLUDE_ADULT_KEY, INCLUDE_ADULT_VALUE)
                .appendQueryParameter(INCLUDE_VIDEO_KEY, INCLUDE_VIDEO_VALUE)
                .appendQueryParameter(PAGE_KEY, PAGE_VALUE)
                .build();

        return builtUri.toString();
    }
}
