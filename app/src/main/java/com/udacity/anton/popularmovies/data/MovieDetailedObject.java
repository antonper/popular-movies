package com.udacity.anton.popularmovies.data;

import android.provider.MediaStore;

/**
 * Created by anton on 06/02/17.
 */

public class MovieDetailedObject {


    private String DataString;
    private String movieTitle;
    private String movieReleaseDate;
    private String movieOverview;
    private String moviePosterPath;
    private String movieVoteAvarage;
    private String movieId;
    private String movieDuration;
    private boolean  isFavorite;

    public VideoObject[] getMovieTrailers() {
        return movieTrailers;
    }

    public void setMovieTrailers(VideoObject[] movieTrailers) {
        this.movieTrailers = movieTrailers;
    }

    public ReviewObject[] getMovieReviews() {
        return movieReviews;
    }

    public void setMovieReviews(ReviewObject[] movieReviews) {
        this.movieReviews = movieReviews;
    }

    private VideoObject[] movieTrailers;
    private ReviewObject[] movieReviews;


    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

    public String getMovieVoteAvarage() {
        return movieVoteAvarage;
    }

    public String getMovieId() {
        return movieId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String getMovieDuration() {
        return movieDuration;
    }

    public MovieDetailedObject(String movieTitle, String movieReleaseDate, String movieOverview, String moviePosterPath, String movieVoteAvarage, String movieId, String movieDuration) {
        this.movieTitle = movieTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.movieOverview = movieOverview;
        this.moviePosterPath = moviePosterPath;
        this.movieVoteAvarage = movieVoteAvarage;
        this.movieId = movieId;
        this.movieDuration=movieDuration;
        this.isFavorite = false;
    }


    public String getDataString() {
        return DataString;
    }

    public void setDataString(String dataString) {
        DataString = dataString;
    }
}
