package com.udacity.anton.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.udacity.anton.popularmovies.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by anton on 05/02/17.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIES_BASE_URL="https://api.themoviedb.org/3/movie";

    private static final String POPULAR_MOVIES_PATH="popular";
    private static final String TOP_MOVIES_PATH="top_rated";

    private static final String TRAILERS_PATH="videos";
    private static final String REVIEWS_PATH="reviews";

    private static final String API_KEY_PARAM="api_key";
    private static final String PAGE_PARAM="page";

    private static final String IMAGE_BASE_URL="http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE_DEFAULT="w185";

    private static final String VIDEO_BASE_URL="https://www.youtube.com/watch";
    private static final String VIDEO_KEY_PARAM="v";


    public static URL buildPopularUrl(String apiKey,int page){
        Uri buildUri=Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(POPULAR_MOVIES_PATH)
                .appendQueryParameter(API_KEY_PARAM,apiKey)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                .build();
        URL url=null;
        try {
            url=new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG,"Build popular url: "+url);
        return url;
    }

    public static URL buildTopUrl(String apiKey,int page){
        Uri buildUri=Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(TOP_MOVIES_PATH)
                .appendQueryParameter(API_KEY_PARAM,apiKey)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                .build();
        URL url=null;
        try {
            url=new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG,"Build top url: "+url);
        return url;
    }

    public static URL buildDetailUrl(String apiKey,String movieId) {
        Uri buildUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Build detail url: " + url);
        return url;
    }


    public static URL buildVideoUrl(String key) {
        Uri buildUri = Uri.parse(VIDEO_BASE_URL).buildUpon()
                .appendQueryParameter(VIDEO_KEY_PARAM, key)
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Build trailer url: " + url);
        return url;
    }
    public static URL buildTrailersUrl(String apiKey, String movieId) {
        Uri buildUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(TRAILERS_PATH)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Build videos url: " + url);
        return url;
    }

    public static URL buildReviewsUrl(String apiKey, String movieId) {
        Uri buildUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Build reviews url: " + url);
        return url;
    }



    public static URL buildImgUrl(String poster_path){
        Uri buildUri=Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE_DEFAULT)
                .appendEncodedPath(poster_path)
                .build();
        URL url=null;
        try {
            url=new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        Log.v(TAG,"Image poster url: "+url);
        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
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
