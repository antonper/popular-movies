package com.udacity.anton.popularmovies.data;

import android.os.AsyncTask;

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

    public class FetchMovieDetails extends AsyncTask<String ,Void, MovieDetailedObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MovieDetailedObject doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(MovieDetailedObject movieDetailedObject) {
            super.onPostExecute(movieDetailedObject);
        }
    }

}
