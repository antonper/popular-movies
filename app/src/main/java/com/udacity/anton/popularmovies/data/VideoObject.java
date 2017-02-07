package com.udacity.anton.popularmovies.data;

import java.net.URL;

/**
 * Created by anton on 06/02/17.
 */

public class VideoObject {

    private URL videoUrl;

    public URL getVideoUrl() {
        return videoUrl;
    }

    public String getName() {
        return name;
    }

    public VideoObject(URL videoUrl, String name) {

        this.videoUrl = videoUrl;
        this.name = name;
    }

    private String name;
}
