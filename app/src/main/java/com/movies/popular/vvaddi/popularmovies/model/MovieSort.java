package com.movies.popular.vvaddi.popularmovies.model;

/**
 * Enum to represents the movie sorting order.
 */

public enum MovieSort {
    MOST_POPULAR("popular"),
    TOP_RATED("top_rated");

    String sortParameter;

    MovieSort(String sortParameter) {
        this.sortParameter = sortParameter;
    }

    public String getSortParameter() {
        return this.sortParameter;
    }
}
