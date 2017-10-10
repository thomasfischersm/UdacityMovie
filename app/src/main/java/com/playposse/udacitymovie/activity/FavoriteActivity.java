package com.playposse.udacitymovie.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * An {@link Activity} that shows the user's favorite movies.
 */
public class FavoriteActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addMainFragment(FavoriteFragment.newInstance());
    }
}
