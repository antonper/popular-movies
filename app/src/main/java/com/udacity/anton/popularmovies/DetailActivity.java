package com.udacity.anton.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.anton.popularmovies.data.MovieDetailedObject;
import com.udacity.anton.popularmovies.data.ReviewObject;
import com.udacity.anton.popularmovies.data.VideoObject;
import com.udacity.anton.popularmovies.utils.MovieDetailJsonUtil;
import com.udacity.anton.popularmovies.utils.NetworkUtils;
import com.udacity.anton.popularmovies.utils.UIUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    public static final String TAG = DetailActivity.class.getSimpleName();
    private String mMovieId;

    @BindView(R.id.title_text_detail)  TextView mTitleTextView;

    @BindView(R.id.progress_bar_detail) ProgressBar mProgressBar;
    @BindView(R.id.data_error_detail) TextView mDataErrorText;
    @BindView(R.id.scroll_detail) ScrollView mScrollView;
    @BindView(R.id.date_text_detail) TextView mDateTextView;
    @BindView(R.id.poster_image_detail) ImageView mPosterView;
    @BindView(R.id.duration_text_detail) TextView mDurationTextView;
    @BindView(R.id.score_text_detail) TextView mScoreTextView;
    @BindView(R.id.overview_text_detail) TextView mOverviewTextView;


    @BindView(R.id.trailer_list_detail) ListView mVideosList;
    private VideoAdapter mVideosArrayAdapter;

    @BindView(R.id.review_list_detail) ListView mReviewsList;
    private ReviewAdapter mReviewsArrayAdapter;


    private String MOVIE_DB_API_KEY;
    private Context mContext;

    private VideoObject[] mVideos;
    private ReviewObject[] mReviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        MOVIE_DB_API_KEY = getString(R.string.moviedbapikey);

        mContext = this;

        Intent startIntent = getIntent();
        if (startIntent != null) {
            if (startIntent.hasExtra(Intent.EXTRA_TEXT)) {
                mMovieId = startIntent.getStringExtra(Intent.EXTRA_TEXT);
            }
        }


        new FetchMovieDetails().execute(mMovieId);



    }

    void showError(){
        mProgressBar.setVisibility(View.INVISIBLE);
        mScrollView.setVisibility(View.INVISIBLE);
        mDataErrorText.setVisibility(View.VISIBLE);
    }
    void showProgress(){
        mProgressBar.setVisibility(View.VISIBLE);
        mScrollView.setVisibility(View.INVISIBLE);
        mDataErrorText.setVisibility(View.INVISIBLE);
    }
    void showData(){
        mProgressBar.setVisibility(View.INVISIBLE);
        mScrollView.setVisibility(View.VISIBLE);
        mDataErrorText.setVisibility(View.INVISIBLE);
    }

    public class FetchMovieDetails extends AsyncTask<String, Void, MovieDetailedObject> {

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected MovieDetailedObject doInBackground(String... params) {
            String movieId = params[0];
            URL detailsUrl = NetworkUtils.buildDetailUrl(MOVIE_DB_API_KEY, movieId);
            URL trailersUrl = NetworkUtils.buildTrailersUrl(MOVIE_DB_API_KEY, movieId);
            URL reviewsUrl = NetworkUtils.buildReviewsUrl(MOVIE_DB_API_KEY, movieId);
            try {
                String jsonMovieDetails = NetworkUtils.getResponseFromHttpUrl(detailsUrl);
                MovieDetailedObject movieDetailedObject = MovieDetailJsonUtil.getMovieDetailedObjectFromJsonString(jsonMovieDetails);

                String jsonTrailers = NetworkUtils.getResponseFromHttpUrl(trailersUrl);
                VideoObject[] videos = MovieDetailJsonUtil.getVideosFromJsonString(jsonTrailers);
                movieDetailedObject.setMovieTrailers(videos);

                String jsonReviews = NetworkUtils.getResponseFromHttpUrl(reviewsUrl);
                ReviewObject[] reviews = MovieDetailJsonUtil.getReviewsFromJsonString(jsonReviews);
                movieDetailedObject.setMovieReviews(reviews);

                return movieDetailedObject;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieDetailedObject movieDetailedObject) {
            if (movieDetailedObject != null) {
                mTitleTextView.setText(movieDetailedObject.getMovieTitle());

                String fullDate = movieDetailedObject.getMovieReleaseDate();
                String year = fullDate.split("-")[0];
                mDateTextView.setText(year);

                Picasso.with(mContext)
                        .load(movieDetailedObject.getMoviePosterPath())
                        .into(mPosterView);

                String minuteAppendix = getString(R.string.minute_appendix);
                mDurationTextView.setText(movieDetailedObject.getMovieDuration() + minuteAppendix);

                String scoreAppendix = getString(R.string.score_appendix);
                mScoreTextView.setText(movieDetailedObject.getMovieVoteAvarage() + scoreAppendix);

                mOverviewTextView.setText(movieDetailedObject.getMovieOverview());

                mVideos = movieDetailedObject.getMovieTrailers();
                Log.v(TAG,"Videos number:"+mVideos.length);
//                mVideosArrayAdapter.setVideos(mVideos);

                mReviews = movieDetailedObject.getMovieReviews();
                Log.v(TAG,"Reviews number:"+mReviews.length);
//                mReviewsArrayAdapter.setReviews(mReviews);

                mVideosArrayAdapter = new VideoAdapter(mContext, mVideos);
                mReviewsArrayAdapter = new ReviewAdapter(mContext, mReviews);

                mReviewsList.setAdapter(mReviewsArrayAdapter);
                UIUtils.setListViewHeightBasedOnItems(mReviewsList);

                mVideosList.setAdapter(mVideosArrayAdapter);
                UIUtils.setListViewHeightBasedOnItems(mVideosList);


                showData();
            }else{
                showError();
            }
        }
    }
}
