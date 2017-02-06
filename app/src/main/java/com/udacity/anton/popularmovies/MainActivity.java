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
import android.widget.TextView;

import com.udacity.anton.popularmovies.data.MovieSimpleObject;
import com.udacity.anton.popularmovies.utils.MovieListJsomUtils;
import com.udacity.anton.popularmovies.utils.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String MOVIE_DB_API_KEY;
    private TextView mTestView;
    //if true-show poular. top_rated if false
    private boolean isPopular;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPopular = true;

        mRecyclerView= (RecyclerView) findViewById(R.id.recycler_view_movies);

        mLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter=new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);


        MOVIE_DB_API_KEY = getString(R.string.moviedbapikey);
        mTestView = (TextView) findViewById(R.id.test_text);
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.movies_order_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id== R.id.movies_order_menu){
            isPopular=!isPopular;
            if(!isPopular){
                item.setTitle(R.string.menu_popular_first_title);
            }else{
                item.setTitle(R.string.menu_top_rated_first_title);
            }
            loadData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        URL dataUrl;
        if (isPopular) {
            dataUrl = NetworkUtils.buildPopularUrl(MOVIE_DB_API_KEY, 1);
        } else {
            dataUrl= NetworkUtils.buildTopUrl(MOVIE_DB_API_KEY,1);
        }
        new FetchMovieData().execute(dataUrl);
        mLayoutManager.scrollToPosition(0);

    }

    @Override
    public void onClick(MovieSimpleObject movieSimpleObject) {
        Log.v(TAG,"Starting new intent on click");
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
        protected MovieSimpleObject[] doInBackground(URL... params) {
            URL url = params[0];
            try {
                String jsonMovieResponce = NetworkUtils.getResponseFromHttpUrl(url);
                return MovieListJsomUtils.getSimpleMovieStringFromJson(MainActivity.this, jsonMovieResponce);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieSimpleObject[] movieSimpleObjectStrings) {
            mMovieAdapter.setmMoviesStrings(movieSimpleObjectStrings);
        }
    }

}

