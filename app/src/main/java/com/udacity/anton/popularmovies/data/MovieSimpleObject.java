package com.udacity.anton.popularmovies.data;

/**
 * Created by anton on 06/02/17.
 */

public class MovieSimpleObject {
    public String getPosterUrl() {
        return posterUrl;
    }

    public String getMovieId() {
        return movieId;
    }

    private String posterUrl;
    private String movieId;

    public MovieSimpleObject(String posterUrl, String movieId) {
        this.posterUrl = posterUrl;
        this.movieId = movieId;
    }

}
