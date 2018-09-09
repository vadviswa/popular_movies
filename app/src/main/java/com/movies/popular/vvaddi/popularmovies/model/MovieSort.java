package com.movies.popular.vvaddi.popularmovies.model;

/**
 * Enum to represents the movie sorting order.
 */

public enum MovieSort {
    MOST_POPULAR("popularity.desc"),
    TOP_RATED("vote_count.desc");

    String sortParameter;

    MovieSort(String sortParameter) {
        this.sortParameter = sortParameter;
    }

    public String getSortParameter() {
        return this.sortParameter;
    }
}
