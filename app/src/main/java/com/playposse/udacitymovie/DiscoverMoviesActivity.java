package com.playposse.udacitymovie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class DiscoverMoviesActivity extends AppCompatActivity {

    private static final String LOG_TAG = DiscoverMoviesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_movies);

        new Thread(new Runnable() {
            @Override
            public void run() {
                TmdbApi tmdbApi = new TmdbApi(Secrets.getMovieApiKeyV3());
                Discover discover = new Discover();
                discover.sortBy("popularity.desc");

                MovieResultsPage resultsPage = tmdbApi.getDiscover().getDiscover(discover);

                for (MovieDb movie : resultsPage.getResults() ) {
                    Log.i(LOG_TAG, "onCreate: Movie is: " + movie.getId() + " " + movie.getImdbID() + " " + movie.getTitle() + " " + " " + movie.getOverview());
                    if (movie.getVideos() != null) {
                        for (Video video : movie.getVideos()) {
                            Log.i(LOG_TAG, "run: Video: " + video.getSite() + " " + video.getType() + " " + video.getKey());
                        }
                    }
                }
            }
        }).start();
    }
}
