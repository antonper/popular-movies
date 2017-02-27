package com.udacity.anton.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.anton.popularmovies.content.MovieContract;
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

    private static final int DB_MOVIE_LOADER_ID = 0;
    private static final int DETAIL_MOVIE_LOADER_ID = 1;

    private static final String MOVIE_ID_KEY = "movie_id";


    private String mMovieId;
    private int mIsFavorite = 0;
    private String mPosterString;

    @BindView(R.id.title_text_detail)
    TextView mTitleTextView;

    @BindView(R.id.progress_bar_detail)
    ProgressBar mProgressBar;
    @BindView(R.id.data_error_detail)
    TextView mDataErrorText;
    @BindView(R.id.scroll_detail)
    ScrollView mScrollView;
    @BindView(R.id.date_text_detail)
    TextView mDateTextView;
    @BindView(R.id.poster_image_detail)
    ImageView mPosterView;
    @BindView(R.id.duration_text_detail)
    TextView mDurationTextView;
    @BindView(R.id.score_text_detail)
    TextView mScoreTextView;
    @BindView(R.id.overview_text_detail)
    TextView mOverviewTextView;

    @BindView(R.id.reviews_title)
    TextView mReviewsTitle;

    @BindView(R.id.trailers_title)
    TextView mTrailersTitle;

    @BindView(R.id.trailer_list_detail)
    ListView mVideosList;
    private VideoAdapter mVideosArrayAdapter;

    @BindView(R.id.review_list_detail)
    ListView mReviewsList;
    private ReviewAdapter mReviewsArrayAdapter;


    private String MOVIE_DB_API_KEY;
    private Context mContext;
    private Button mFavoriteButton;

    private VideoObject[] mVideos;
    private LoaderManager.LoaderCallbacks<Cursor> dbLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(mContext) {

                // Initialize a Cursor, this will hold all the task data
                Cursor mMovieDbData = null;

                // onStartLoading() is called when a loader first starts loading data
                @Override
                protected void onStartLoading() {
                    if (mMovieDbData != null) {
                        // Delivers any previously loaded data immediately
                        deliverResult(mMovieDbData);
                    } else {
                        // Force a new load
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {
                    Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(mMovieId).build();
                    Cursor returnCur;
                    try {
                        returnCur = getContentResolver().query(uri,
                                null,
                                null,
                                null,
                                null);

                    } catch (Exception e) {
                        Log.e(TAG, "Failed to asynchronously load data.");
                        e.printStackTrace();
                        return null;
                    }

                    Log.v(TAG, "Cursor count in details" + returnCur.getCount());

                    if (returnCur.getCount() == 0) {

                    }
                    return returnCur;
                }

                // deliverResult sends the result of the load, a Cursor, to the registered listener
                public void deliverResult(Cursor data) {
                    mMovieDbData = data;
                    super.deliverResult(data);
                }
            };

        }


        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null && data.getCount() > 0) {
                int columnFavorite = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE);
                int columnPoster = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
                data.moveToFirst();
                mIsFavorite = data.getInt(columnFavorite);
//            mPosterString= data.getString(columnPoster);
            }
            if (mIsFavorite == 1) {
                buttonSetFavorite();
            }
//        Toast.makeText(mContext, "favorite:" + mIsFavorite, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }


    };
    private LoaderManager.LoaderCallbacks<MovieDetailedObject> movieDetailLoaderListenter = new LoaderManager.LoaderCallbacks<MovieDetailedObject>() {

        @Override
        public Loader<MovieDetailedObject> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<MovieDetailedObject>(mContext) {

                MovieDetailedObject movieDetailedObject;

                @Override
                protected void onStartLoading() {
                    showProgress();
                    if (movieDetailedObject != null) {
                        deliverResult(movieDetailedObject);

                        showData();

                    } else {
                        forceLoad();
                    }
                }

                @Override
                public MovieDetailedObject loadInBackground() {
                    URL detailsUrl = NetworkUtils.buildDetailUrl(MOVIE_DB_API_KEY, mMovieId);
                    URL trailersUrl = NetworkUtils.buildTrailersUrl(MOVIE_DB_API_KEY, mMovieId);
                    URL reviewsUrl = NetworkUtils.buildReviewsUrl(MOVIE_DB_API_KEY, mMovieId);
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
                public void deliverResult(MovieDetailedObject data) {
                    movieDetailedObject = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<MovieDetailedObject> loader, MovieDetailedObject movieDetailedObject) {
            if (movieDetailedObject != null) {
                mTitleTextView.setText(movieDetailedObject.getMovieTitle());

                String fullDate = movieDetailedObject.getMovieReleaseDate();
                String year = fullDate.split("-")[0];
                mDateTextView.setText(year);

                mPosterString = movieDetailedObject.getMoviePosterPath();

                Picasso.with(mContext)
                        .load(movieDetailedObject.getMoviePosterPath())
                        .into(mPosterView);

                String minuteAppendix = getString(R.string.minute_appendix);
                mDurationTextView.setText(movieDetailedObject.getMovieDuration() + minuteAppendix);

                String scoreAppendix = getString(R.string.score_appendix);
                mScoreTextView.setText(movieDetailedObject.getMovieVoteAvarage() + scoreAppendix);

                mOverviewTextView.setText(movieDetailedObject.getMovieOverview());

                mVideos = movieDetailedObject.getMovieTrailers();
                Log.v(TAG, "Videos number:" + mVideos.length);
//                mVideosArrayAdapter.setVideos(mVideos);
                if (mVideos.length == 0) {
                    mTrailersTitle.setText(getString(R.string.no_trailers_title));
                }

                mReviews = movieDetailedObject.getMovieReviews();
                Log.v(TAG, "Reviews number:" + mReviews.length);
                if (mReviews.length == 0) {
                    mReviewsTitle.setText(getString(R.string.no_reviews_title));
                }
//                mReviewsArrayAdapter.setReviews(mReviews);

                mVideosArrayAdapter = new VideoAdapter(mContext, mVideos);
                mReviewsArrayAdapter = new ReviewAdapter(mContext, mReviews);

                mReviewsList.setAdapter(mReviewsArrayAdapter);
                UIUtils.setListViewHeightBasedOnItems(mReviewsList);

                mVideosList.setAdapter(mVideosArrayAdapter);
                UIUtils.setListViewHeightBasedOnItems(mVideosList);

                mReviewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ReviewObject review = (ReviewObject) mReviewsList.getItemAtPosition(position);
//                        Toast.makeText(mContext,review.getReviewUrl(),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(review.getReviewUrl()));
                        startActivity(intent);
                    }
                });
                mVideosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        VideoObject video = (VideoObject) mVideosList.getItemAtPosition(position);
//                        Toast.makeText(mContext,video.getVideoUrl().toString(),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(video.getVideoUrl().toString()));
                        startActivity(intent);
                    }
                });

                showData();
            } else {
                showError();
            }

        }

        @Override
        public void onLoaderReset(Loader<MovieDetailedObject> loader) {

        }
    };


    private ReviewObject[] mReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        MOVIE_DB_API_KEY = getString(R.string.moviedbapikey);

        mContext = this;

        mFavoriteButton = (Button) findViewById(R.id.favorite_button);
        Intent startIntent = getIntent();
        if (startIntent != null) {
            if (startIntent.hasExtra(Intent.EXTRA_TEXT)) {
                mMovieId = startIntent.getStringExtra(Intent.EXTRA_TEXT);
            }
        }


        Bundle movieDetailBundle = new Bundle();
        movieDetailBundle.putString(MOVIE_ID_KEY, mMovieId);


        if (NetworkUtils.isNetworkAvailable(mContext)) {
            loadData();
        } else {
            Toast.makeText(mContext, getString(R.string.no_data_received), Toast.LENGTH_SHORT).show();
        }


    }

    void loadData() {
        LoaderManager loaderManager = getSupportLoaderManager();

        Loader<MovieDetailedObject> movieDetailLoader = loaderManager.getLoader(DETAIL_MOVIE_LOADER_ID);
        Loader<Cursor> dbLoader = loaderManager.getLoader(DB_MOVIE_LOADER_ID);
        if (movieDetailLoader == null) {
            loaderManager.initLoader(DETAIL_MOVIE_LOADER_ID, null, movieDetailLoaderListenter);
        } else {
            loaderManager.restartLoader(DETAIL_MOVIE_LOADER_ID, null, movieDetailLoaderListenter);
        }

        if (dbLoader == null) {
            loaderManager.initLoader(DB_MOVIE_LOADER_ID, null, dbLoaderListener);
        } else {
            loaderManager.restartLoader(DB_MOVIE_LOADER_ID, null, dbLoaderListener);
        }
    }

    void showError() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mScrollView.setVisibility(View.INVISIBLE);
        mDataErrorText.setVisibility(View.VISIBLE);
    }

    void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mScrollView.setVisibility(View.INVISIBLE);
        mDataErrorText.setVisibility(View.INVISIBLE);
    }

    void showData() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mScrollView.setVisibility(View.VISIBLE);
        mDataErrorText.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(DB_MOVIE_LOADER_ID, null, dbLoaderListener);
    }

    public void buttonSetFavorite() {
        mFavoriteButton.setText(getText(R.string.remove_mark_favorite));
        mFavoriteButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorButtonPressed));
    }

    public void buttonUnSetFavorite() {
        mFavoriteButton.setText(getText(R.string.mark_as_favorite));
        mFavoriteButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorButton));
    }

    public void onClickFavorite(View view) {

        if (mIsFavorite == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry._ID, mMovieId);
            contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, mPosterString);
            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
            if (uri != null) {
                mIsFavorite = 1;
                buttonSetFavorite();
//                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(mMovieId).build();
            if (getContentResolver().delete(uri, null, null) > 0) {
                buttonUnSetFavorite();
                mIsFavorite = 0;
            } else {
                Toast.makeText(mContext, "Ooops, cant delete it", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
