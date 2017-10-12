package com.playposse.udacitymovie;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.playposse.udacitymovie.data.MovieDatabaseHelper;
import com.playposse.udacitymovie.receiver.NetworkReceiver;
import com.playposse.udacitymovie.service.BuildMovieCacheService;

/**
 * Implementation of the {@link Application}.
 */
public class UdacityMovieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        getApplicationContext().deleteDatabase(MovieDatabaseHelper.DB_NAME);

        // Listen to network changes.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkReceiver(), intentFilter);

        // Cache movie data.
        startService(new Intent(getApplicationContext(), BuildMovieCacheService.class));
    }
}
