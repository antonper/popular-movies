package com.udacity.anton.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.udacity.anton.popularmovies.data.MovieSimpleObject;
import com.udacity.anton.popularmovies.utils.MovieListJsonUtils;
import com.udacity.anton.popularmovies.utils.NetworkUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String MOVIE_DB_API_KEY;

    //popular:0
    //top:1
    //fav:2
    private int mMode;

    private static int PAGE_LIMIT = 100;

    private static final String PAGE_KEY = "page";

    private static final int MOVIES_LOADER_ID = 10;

    @BindView(R.id.no_internet_text)
    TextView mNoData;
    @BindView(R.id.recycler_view_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private MovieAdapter mMovieAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private EndlessRecyclerViewScrollListener endlessScrollListener;

    private Context mContext;


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
                        mDataUrl = NetworkUtils.buildTopUrl(MOVIE_DB_API_KEY, page
                        );
                    }

                    if (modeOld == mMode && mMovies != null && mPage > page) {
                        Log.v(TAG, " Delivering result");
                        deliverResult(mMovies);
                    } else {
                        Log.v(TAG, " Loading page:" + page);
                        mPage = page;
                        forceLoad();
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
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<MovieSimpleObject[]> loader, MovieSimpleObject[] data) {
            mMovieAdapter.appendMovies(data);
            if (data == null) {
                showError();
            } else {
                showData();
            }

        }

        @Override
        public void onLoaderReset(Loader<MovieSimpleObject[]> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = this;

        mMode = 0;
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        endlessScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                if (page <= PAGE_LIMIT) loadData(page);
            }

        };
        mRecyclerView.addOnScrollListener(endlessScrollListener);


        MOVIE_DB_API_KEY = getString(R.string.moviedbapikey);
        loadData(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //TODO make 3 buttond but display 2 of them
        if (id == R.id.movies_order_menu) {
            mMode = 1 - mMode;
            Log.v(TAG, "Mode is:" + mMode);
            if (mMode == 0) {
                item.setTitle(R.string.menu_popular_first_title);
            } else if (mMode == 1) {
                item.setTitle(R.string.menu_top_rated_first_title);
            }
            mMovieAdapter.clearMovies();
            endlessScrollListener.resetState();
            loadData(1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData(int page) {
//        URL dataUrl = null;
//        if (mMode == 1) {
//            dataUrl = NetworkUtils.buildPopularUrl(MOVIE_DB_API_KEY, page);
//        } else if (mMode == 0) {
//            dataUrl = NetworkUtils.buildTopUrl(MOVIE_DB_API_KEY, page);
//        }
//        new FetchMovieData().execute(dataUrl);

        Bundle bundle = new Bundle();
        bundle.putInt(PAGE_KEY, page);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<MovieSimpleObject[]> dataLoader = loaderManager.getLoader(MOVIES_LOADER_ID);
        if (dataLoader==null) {
            loaderManager.initLoader(MOVIES_LOADER_ID, bundle, moviesLoaderListener);
        } else {
            loaderManager.restartLoader(MOVIES_LOADER_ID, bundle, moviesLoaderListener);
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
    public void onClick(MovieSimpleObject movieSimpleObject) {
        Log.v(TAG, "Starting new intent on click");
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movieSimpleObject.getMovieId());
        startActivity(intentToStartDetailActivity);
    }

}

