package com.playposse.udacitymovie.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * An {@link Activity} that shows the movie details.
 */
public class MovieActivity extends ParentActivity {

    public static final String MOVIE_ID_EXTRA_CONSTANT = "movieId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addMainFragment(MovieFragment.newInstance(getIntent()));
    }
}
