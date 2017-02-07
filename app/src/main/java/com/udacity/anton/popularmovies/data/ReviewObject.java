package com.udacity.anton.popularmovies.data;

/**
 * Created by anton on 06/02/17.
 */

public class ReviewObject {
    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }

    private String reviewContent;
    private String reviewAuthor;
    private String reviewUrl;

    public ReviewObject(String reviewContent, String reviewAuthor, String reviewUrl) {
        this.reviewContent = reviewContent;
        this.reviewAuthor = reviewAuthor;
        this.reviewUrl = reviewUrl;
    }


}
