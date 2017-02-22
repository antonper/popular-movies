package com.udacity.anton.popularmovies.content;

import android.net.Uri;

/**
 * Created by toshnh on 22.02.17.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.udacity.anton.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String MOVIES_PATH = "movies";

    public static final class MovieEntry {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();

        public static final String TABLE_NAME = "movies";
        public static final String _ID = "_id";
        public static final String COLUMN_FAVORITE = "favorite";
    }
}
