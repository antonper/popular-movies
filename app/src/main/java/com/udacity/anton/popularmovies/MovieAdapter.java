package com.udacity.anton.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.anton.popularmovies.data.MovieSimpleObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anton on 05/02/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {


    private static final String TAG = MovieAdapter.class.getSimpleName();
    private List<MovieSimpleObject> mMoviesData;
    private Context mContext;

    private final MovieAdapterOnClickHandler mMovieAdapterOnClickHandler;

    public MovieAdapter(MovieAdapterOnClickHandler movieAdapterOnClickHandler) {
        mMovieAdapterOnClickHandler = movieAdapterOnClickHandler;
        mMoviesData = new ArrayList<>();
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        int layoutIdForListItem = R.layout.recler_view_movies_items;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediatly = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediatly);
        return new MovieAdapterViewHolder(view);


    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        String posterUrl = mMoviesData.get(position).getPosterUrl();

        Picasso.with(mContext)
                .load(posterUrl)
                .into(holder.poster);


//        holder.textView.setText(posterUrl);

    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) return 0;
        return mMoviesData.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView poster;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.posterImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();
            Log.v(TAG, "clicked " + adapterPosition);
            MovieSimpleObject movieSimpleObject = mMoviesData.get(adapterPosition);
            mMovieAdapterOnClickHandler.onClick(movieSimpleObject);
        }
    }

    public void setMoviesStrings(MovieSimpleObject[] movies) {
        this.mMoviesData.clear();
        this.mMoviesData.addAll(Arrays.asList(movies));
        notifyDataSetChanged();
    }

    public void appendMovies(MovieSimpleObject[] movies) {
        mMoviesData.addAll(Arrays.asList(movies));
        notifyDataSetChanged();

    }

    public void clearMovies(){
        mMoviesData.clear();
         notifyDataSetChanged();
    }


    public interface MovieAdapterOnClickHandler {
        void onClick(MovieSimpleObject movieSimpleObject);
    }
}
