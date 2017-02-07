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

import java.net.URL;

public class DetailActivity extends AppCompatActivity {
    public static final String TAG = DetailActivity.class.getSimpleName();
    private String mMovieId;
    private TextView mTitleTextView;
    private TextView mDateTextView;
    private TextView mDurationTextView;
    private ImageView mPosterView;
    private TextView mScoreTextView;
    private TextView mOverviewTextView;
    private ScrollView mScrollView;
    private ProgressBar mProgressBar;
    private TextView mDataErrorText;

    private LinearLayout mVideosList;
    private VideoAdapter mVideosArrayAdapter;

    private LinearLayout mReviewsList;
    private ReviewAdapter mReviewsArrayAdapter;


    private String MOVIE_DB_API_KEY;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MOVIE_DB_API_KEY = getString(R.string.moviedbapikey);
        mProgressBar= (ProgressBar) findViewById(R.id.progress_bar_detail);
        mDataErrorText= (TextView) findViewById(R.id.data_error_detail);
        mScrollView= (ScrollView) findViewById(R.id.scroll_detail);
        mTitleTextView = (TextView) findViewById(R.id.title_text_detail);
        mDateTextView = (TextView) findViewById(R.id.date_text_detail);
        mPosterView = (ImageView) findViewById(R.id.poster_image_detail);
        mDurationTextView = (TextView) findViewById(R.id.duration_text_detail);
        mScoreTextView = (TextView) findViewById(R.id.score_text_detail);
        mOverviewTextView = (TextView) findViewById(R.id.overview_text_detail);

        mVideosList = (LinearLayout) findViewById(R.id.trailer_list_detail);
        mReviewsList = (LinearLayout) findViewById(R.id.review_list_detail);


        mContext = this;

        Intent startIntent = getIntent();
        if (startIntent != null) {
            if (startIntent.hasExtra(Intent.EXTRA_TEXT)) {
                mMovieId = startIntent.getStringExtra(Intent.EXTRA_TEXT);
//                mTextView.setText(mMovieId);
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

                VideoObject[] videos = movieDetailedObject.getMovieTrailers();
                mVideosArrayAdapter = new VideoAdapter(mContext, videos);
                for (int i = 0; i < mVideosArrayAdapter.getCount(); i++) {
                    final int index = i;
                    View view = mVideosArrayAdapter.getView(i, null, mVideosList);
                    view.setOnClickListener(new View.OnClickListener() {


                        @Override
                        public void onClick(View v) {
                            Log.v(TAG, "videos clicked " + index);
                        }
                    });

                    mVideosList.addView(view);
                }

                ReviewObject[] reviews = movieDetailedObject.getMovieReviews();
                mReviewsArrayAdapter = new ReviewAdapter(mContext, reviews);
                for (int i = 0; i < mReviewsArrayAdapter.getCount(); i++) {
                    final int index = i;
                    View view = mReviewsArrayAdapter.getView(i, null, mReviewsList);
                    view.setOnClickListener(new View.OnClickListener() {


                        @Override
                        public void onClick(View v) {
                            Log.v(TAG, "review clicked " + index);
                        }
                    });
                    mReviewsList.addView(view);
                }
                showData();
            }else{
                showError();
            }
        }
    }
}
