package com.udacity.anton.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.udacity.anton.popularmovies.data.ReviewObject;

/**
 * Created by anton on 07/02/17.
 */

class ReviewAdapter extends ArrayAdapter<ReviewObject> {

    private ReviewObject[] reviews;

    public ReviewAdapter(Context context, ReviewObject[] reviews) {
        super(context, 0, reviews);
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReviewObject review = getItem(position);
        ReviewViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, parent, false);
            holder = new ReviewViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.review_item_text);
            convertView.setTag(holder);
        } else {
            holder = (ReviewViewHolder) convertView.getTag();
        }

        if (review != null) {
            holder.text.setText(review.getReviewAuthor());
        }

        return convertView;
    }

    void setReviews(ReviewObject[] reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }


    static class ReviewViewHolder {
        TextView text;
    }

    public ReviewObject getReview(int pos) {
        return reviews[pos];
    }

}
