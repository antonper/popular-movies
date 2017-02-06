package com.udacity.anton.popularmovies.utils;

import android.content.Context;

import com.udacity.anton.popularmovies.data.MovieSimpleObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by anton on 05/02/17.
 */

public final class MovieListJsomUtils {

    public static MovieSimpleObject[] getSimpleMovieStringFromJson(Context context, String movieListJsonString) throws JSONException {
        final String MDB_RESULTS="results";
        final String MDB_POSTER="poster_path";
        final String MDB_TITLE="title";
        final String MDB_ID="id";
        JSONObject movieListJson=new JSONObject(movieListJsonString);

        JSONArray movieArray=movieListJson.getJSONArray(MDB_RESULTS);

        MovieSimpleObject[] parcedData=new MovieSimpleObject[movieArray.length()];

        for(int i=0;i<movieArray.length();i++){
            String movie_poster;
            String movie_id;
            String movie_title;


            JSONObject movieJson=movieArray.getJSONObject(i);
            movie_title=movieJson.getString(MDB_TITLE);

            String poster_path=movieJson.getString(MDB_POSTER);
            movie_poster= String.valueOf(NetworkUtils.buildImgUrl(poster_path));

            movie_id=movieJson.getString(MDB_ID);



            parcedData[i] = new MovieSimpleObject(movie_poster,movie_id);
        }
        return parcedData;
    }

}
