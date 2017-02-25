package com.udacity.anton.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.anton.popularmovies.content.MovieContract;
import com.udacity.anton.popularmovies.data.MovieSimpleObject;
import com.udacity.anton.popularmovies.utils.MovieListJsonUtils;
import com.udacity.anton.popularmovies.utils.NetworkUtils;
import com.udacity.anton.popularmovies.utils.UIUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String MOVIE_DB_API_KEY;

    //popular:0
    //top:1
    //fav:2
    private int mMode;

    private Menu mMenu;
    private static int PAGE_LIMIT = 100;

    private static final String PAGE_KEY = "page";

    private static final String STATE_SAVE_KEY = "state";
    private static final String MODE_SAVE_KEY = "mode";
    private static final String PAGE_SAVE_KEY = "page";

    private int mPage = 1;
    private Parcelable mState;

    private static final int MOVIES_REMOTE_LOADER_ID = 10;
    private static final int MOVIES_DB_LOADER_ID = 11;

    private boolean mDataHasChanged = false;


    @BindView(R.id.no_internet_text)
    TextView mNoData;
    @BindView(R.id.recycler_view_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private MovieAdapter mMovieAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
//    private EndlessRecyclerViewScrollListener endlessScrollListener;

    private Context mContext;
    private int mColumnNumber;


    private LoaderManager.LoaderCallbacks<MovieSimpleObject[]> moviesLoaderListener = new LoaderManager.LoaderCallbacks<MovieSimpleObject[]>() {
        @Override
        public Loader<MovieSimpleObject[]> onCreateLoader(int id, final Bundle args) {

            return new AsyncTaskLoader<MovieSimpleObject[]>(mContext) {
                private URL mDataUrl = null;
                private int mPage;

                private int modeOld;
                MovieSimpleObject[] mMovies;

                @Override
                protected void onStartLoading() {
                    showProgress();
                    int page = args.getInt(PAGE_KEY);
                    if (mMode == 1) {
                        mDataUrl = NetworkUtils.buildPopularUrl(MOVIE_DB_API_KEY, page);
                    } else if (mMode == 0) {
                        mDataUrl = NetworkUtils.buildTopUrl(MOVIE_DB_API_KEY, page);
                    }

                    if (mMode != 2) {
                        if (modeOld == mMode && mMovies != null && mPage > page) {
                            deliverResult(mMovies);
                        } else {
                            Log.v(TAG, " Loading page:" + page);
                            mPage = page;
                            forceLoad();
                        }
                    }
                    modeOld = mMode;
                }

                @Override
                public MovieSimpleObject[] loadInBackground() {
                    try {
                        String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(mDataUrl);
                        return MovieListJsonUtils.getSimpleMovieStringFromJson(MainActivity.this, jsonMovieResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                public void deliverResult(MovieSimpleObject[] data) {
                    mMovies = data;
                    Log.v(TAG, " Delivering result");
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<MovieSimpleObject[]> loader, MovieSimpleObject[] data) {
            if (data == null) {
                showError();
            } else {
                mMovieAdapter.appendMovies(data);
                showData();
                finishLoad();
            }

        }

        @Override
        public void onLoaderReset(Loader<MovieSimpleObject[]> loader) {

        }
    };
    private LoaderManager.LoaderCallbacks<Cursor> dbLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(mContext) {

                // Initialize a Cursor, this will hold all the task data
                Cursor mMovieDbData = null;

                // onStartLoading() is called when a loader first starts loading data
                @Override
                protected void onStartLoading() {
                    // Force a new load
                    if (mMode == 2) {
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {
                    Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().build();
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

                    Log.v(TAG, "Cursor count " + returnCur.getCount());

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

            if (data == null) {
                showError();
            } else {
                int columnId = data.getColumnIndex(MovieContract.MovieEntry._ID);
                int columnPoster = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);

                MovieSimpleObject[] movies = new MovieSimpleObject[data.getCount()];

                while (data.moveToNext()) {
                    int id = data.getInt(columnId);
                    String poster = data.getString(columnPoster);
                    movies[data.getPosition()] = new MovieSimpleObject(poster, Integer.toString(id));
                }
                mMovieAdapter.appendMovies(movies);
                showData();
                finishLoad();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = MainActivity.this;

        mMode = 0;
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MODE_SAVE_KEY)) {
                mMode = savedInstanceState.getInt(MODE_SAVE_KEY);
            }
            if (savedInstanceState.containsKey(PAGE_SAVE_KEY)) {
                mPage = savedInstanceState.getInt(PAGE_SAVE_KEY);
            }
            if (savedInstanceState.containsKey(STATE_SAVE_KEY)) {
                mState = savedInstanceState.getParcelable(STATE_SAVE_KEY);
            }

            Log.v(TAG, "SavedInstances: Mode:" + mMode + " page:" + mPage);
        }





        mMovieAdapter = new MovieAdapter(this);
//        endlessScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) mLayoutManager,mPage) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                if (mMode != 2 && page <= PAGE_LIMIT) loadData(page);
//            }
//
//        };
//        mRecyclerView.addOnScrollListener(endlessScrollListener);


        MOVIE_DB_API_KEY = getString(R.string.moviedbapikey);
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            mMovieAdapter.clearMovies();
            if (mMode == 2) {
                loadData(1);
            } else {
                for (int i = 1; i <= mPage; i++) {
                    loadData(i);
                }
            }


        } else {
            Toast.makeText(mContext, getString(R.string.no_data_received), Toast.LENGTH_SHORT).show();
        }


    }


    void finishLoad(){
        int mColumnNumber = UIUtils.calculateNoOfColumns(mContext);
        mLayoutManager = new GridLayoutManager(MainActivity.this, mColumnNumber);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMovieAdapter);
        if (mState != null) {
            Log.v(TAG, "restoring state");
            mLayoutManager.onRestoreInstanceState(mState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_order_menu, menu);
        this.mMenu = menu;
        mMenu.setGroupVisible(R.id.order_menu_group, true);
        switch (mMode) {
            case 0:
                mMenu.findItem(R.id.movies_popular_menu).setVisible(false);
                break;
            case 1:
                mMenu.findItem(R.id.movies_top_rated_menu).setVisible(false);
                break;
            case 2:
                mMenu.findItem(R.id.movies_favorite_menu).setVisible(false);
                break;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.movies_top_rated_menu) {
            mMode = 1;
            mMovieAdapter.clearMovies();
//            endlessScrollListener.resetState();
            loadData(1);
            mMenu.setGroupVisible(R.id.order_menu_group, true);
            item.setVisible(false);
            return true;
        } else if (id == R.id.movies_popular_menu) {
            mMode = 0;
            mMovieAdapter.clearMovies();
//            endlessScrollListener.resetState();
            mMenu.setGroupVisible(R.id.order_menu_group, true);
            item.setVisible(false);
            loadData(1);
        } else if (id == R.id.movies_favorite_menu) {
            mMode = 2;
            mMovieAdapter.clearMovies();
//            endlessScrollListener.resetState();
            loadData(1);
            mMenu.setGroupVisible(R.id.order_menu_group, true);
            item.setVisible(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadData(int page) {

        mPage = page;
        Log.v(TAG, "MODE:" + mMode + " page:" + page);
//        URL dataUrl = null;
//        if (mMode == 1) {
//            dataUrl = NetworkUtils.buildPopularUrl(MOVIE_DB_API_KEY, page);
//        } else if (mMode == 0) {
//            dataUrl = NetworkUtils.buildTopUrl(MOVIE_DB_API_KEY, page);
//        }
//        new FetchMovieData().execute(dataUrl);
        LoaderManager loaderManager = getSupportLoaderManager();

        if (mMode != 2) {
            Bundle bundle = new Bundle();
            bundle.putInt(PAGE_KEY, page);


            Loader<MovieSimpleObject[]> dataLoader = loaderManager.getLoader(MOVIES_REMOTE_LOADER_ID);
            if (dataLoader == null) {
                loaderManager.initLoader(MOVIES_REMOTE_LOADER_ID, bundle, moviesLoaderListener);
            } else {
                loaderManager.restartLoader(MOVIES_REMOTE_LOADER_ID, bundle, moviesLoaderListener);
            }
        } else {
            Loader<Cursor> dbLoader = loaderManager.getLoader(MOVIES_DB_LOADER_ID);
            if (dbLoader == null) {
                loaderManager.initLoader(MOVIES_DB_LOADER_ID, null, dbLoaderListener);
            } else {
                loaderManager.restartLoader(MOVIES_DB_LOADER_ID, null, dbLoaderListener);
            }
        }
        //mLayoutManager.scrollToPosition(0);
    }

    void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mNoData.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    void showData() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mNoData.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    void showError() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mNoData.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onRestart() {
        Log.v(TAG, "Mode is :" + mMode);
        mMovieAdapter.clearMovies();

        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MODE_SAVE_KEY, mMode);
        outState.putParcelable(STATE_SAVE_KEY, mLayoutManager.onSaveInstanceState());
        outState.putInt(PAGE_SAVE_KEY, mPage);
    }


    @Override
    public void onClick(MovieSimpleObject movieSimpleObject) {
        Log.v(TAG, "Starting new intent on click");
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movieSimpleObject.getMovieId());
        startActivity(intentToStartDetailActivity);
    }

}

