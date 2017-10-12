package com.playposse.udacitymovie.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.playposse.udacitymovie.R;

/**
 * An {@link Activity} that parks the user until network connectivity has been restored.
 */
public class NoNetworkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_no_network);
    }
}
