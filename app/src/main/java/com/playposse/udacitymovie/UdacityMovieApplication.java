package com.playposse.udacitymovie;

import android.app.Application;
import android.content.Intent;

import com.playposse.udacitymovie.data.MovieDatabaseHelper;
import com.playposse.udacitymovie.services.BuildMovieCacheService;

/**
 * Implementation of the {@link Application}.
 */
public class UdacityMovieApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        getApplicationContext().deleteDatabase(MovieDatabaseHelper.DB_NAME);

        startService(new Intent(getApplicationContext(), BuildMovieCacheService.class));
    }
}
