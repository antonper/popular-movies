package com.udacity.anton.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private String mMovieId;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTextView=(TextView)findViewById(R.id.movie_detail_id_text);
        Intent startIntent=getIntent();
        if (startIntent != null) {
            if (startIntent.hasExtra(Intent.EXTRA_TEXT)) {
                mMovieId = startIntent.getStringExtra(Intent.EXTRA_TEXT);
                mTextView.setText(mMovieId);
            }
        }
    }
}
