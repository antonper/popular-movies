package com.udacity.anton.popularmovies.utils;

import android.util.Log;

import com.udacity.anton.popularmovies.data.MovieDetailedObject;
import com.udacity.anton.popularmovies.data.ReviewObject;
import com.udacity.anton.popularmovies.data.VideoObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


public class MovieDetailJsonUtil {

    private static final String TAG=MovieDetailJsonUtil.class.getSimpleName();
    public static MovieDetailedObject getMovieDetailedObjectFromJsonString(String movieJsonString) throws JSONException {
        final String MOVIE_TITLE = "title";
        final String MOVIE_SCORE = "vote_average";
        final String MOVIE_DATE = "release_date";
        final String MOVIE_DURATION = "runtime";
        final String MOVIE_ID = "id";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_POSTER = "poster_path";

        JSONObject movieJson = new JSONObject(movieJsonString);
        String title = movieJson.getString(MOVIE_TITLE);
        String score = movieJson.getString(MOVIE_SCORE);
        String date = movieJson.getString(MOVIE_DATE);
        String duration = movieJson.getString(MOVIE_DURATION);
        String overview = movieJson.getString(MOVIE_OVERVIEW);

        String id = movieJson.getString(MOVIE_ID);
        String posterPath = movieJson.getString(MOVIE_POSTER);
        String poster = String.valueOf(NetworkUtils.buildImgUrl(posterPath));


        MovieDetailedObject movieDetailedObject = new MovieDetailedObject(title, date, overview, poster, score, id,duration);
        movieDetailedObject.setDataString(movieJsonString);

        return movieDetailedObject;
    }

    public static VideoObject[] getVideosFromJsonString(String videosJsonString) throws JSONException {
        final String VIDEOS_RESULTS = "results";
        final String VIDEOS_WEBSITE = "site";
        final String VIDEOS_KEY = "key";
        final String VIDEOS_NAME = "name";
        JSONObject videosJson = new JSONObject(videosJsonString);

        JSONArray videosJsonArray = videosJson.getJSONArray(VIDEOS_RESULTS);

        VideoObject[] videosArray = new VideoObject[videosJsonArray.length()];

        for (int i = 0; i < videosJsonArray.length(); i++) {
            String videoKey;
            String videoName;
            String videoSite;


            JSONObject videoJson = videosJsonArray.getJSONObject(i);
            videoName = videoJson.getString(VIDEOS_NAME);
            videoSite = videoJson.getString(VIDEOS_WEBSITE);
            videoKey = videoJson.getString(VIDEOS_KEY);
            if (videoSite.equals("YouTube")) {
                URL videoUrl = NetworkUtils.buildVideoUrl(videoKey);
                videosArray[i] = new VideoObject(videoUrl, videoName);
            } else {
                Log.v(TAG,"web site "+videoSite+" is unknown");
            }
        }
        return videosArray;

    }

    public static ReviewObject[] getReviewsFromJsonString(String reviewsJsonString) throws JSONException {
        final String REVIEWS_RESULTS = "results";
        final String REVIEWS_CONTENT = "content";
        final String REVIEWS_AUTHOR = "author";
        final String REVIEWS_URL = "url";
        JSONObject reviewsJson = new JSONObject(reviewsJsonString);

        JSONArray reviewsJsonArray = reviewsJson.getJSONArray(REVIEWS_RESULTS);

        ReviewObject[] reviewsArray = new ReviewObject[reviewsJsonArray.length()];

        for (int i = 0; i < reviewsJsonArray.length(); i++) {
            String reviewContent;
            String reviewAuthor;
            String reviewUrl;


            JSONObject reviewJson = reviewsJsonArray.getJSONObject(i);
            reviewContent = reviewJson.getString(REVIEWS_CONTENT);
            reviewAuthor = reviewJson.getString(REVIEWS_AUTHOR);
            reviewUrl = reviewJson.getString(REVIEWS_URL);
            reviewsArray[i] = new ReviewObject(reviewContent, reviewAuthor,reviewUrl);
        }
        return reviewsArray;

    }
}
