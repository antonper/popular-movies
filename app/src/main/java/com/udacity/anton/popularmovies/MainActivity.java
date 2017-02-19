package com.udacity.anton.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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

    //if true-show popular. top_rated if false
    private boolean isPopular;

    private static int PAGE_LIMIT = 100;

    @BindView(R.id.no_internet_text)
    TextView mNoData;
    @BindView(R.id.recycler_view_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private MovieAdapter mMovieAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private EndlessRecyclerViewScrollListener endlessScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        isPopular = true;
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        endlessScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (page <= PAGE_LIMIT) loadData(page);
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

        if (id == R.id.movies_order_menu) {
            isPopular = !isPopular;
            if (!isPopular) {
                item.setTitle(R.string.menu_popular_first_title);
            } else {
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
        URL dataUrl;
        if (isPopular) {
            dataUrl = NetworkUtils.buildPopularUrl(MOVIE_DB_API_KEY, page);
        } else {
            dataUrl = NetworkUtils.buildTopUrl(MOVIE_DB_API_KEY, page);
        }
        new FetchMovieData().execute(dataUrl);
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

    public class FetchMovieData extends AsyncTask<URL, Void, MovieSimpleObject[]> {

        FetchMovieData() {
        }

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected MovieSimpleObject[] doInBackground(URL... params) {
            URL url = params[0];
            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(url);
                return MovieListJsonUtils.getSimpleMovieStringFromJson(MainActivity.this, jsonMovieResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieSimpleObject[] movieSimpleObjectStrings) {
            mMovieAdapter.appendMovies(movieSimpleObjectStrings);
            if (movieSimpleObjectStrings == null) {
                showError();
            } else {
                showData();
            }
        }
    }


}

