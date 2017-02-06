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

    private static final String API_KEY_PARAM="api_key";
    private static final String PAGE_PARAM="page";

    private static final String IMAGE_BASE_URL="http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE_DEFAULT="w185";

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

        Log.v(TAG,"Image poster url: "+url);
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
