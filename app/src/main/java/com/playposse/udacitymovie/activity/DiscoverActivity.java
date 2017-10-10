package com.playposse.udacitymovie.activity;

import android.os.Bundle;
import android.util.Log;

import com.playposse.udacitymovie.R;
import com.playposse.udacitymovie.Secrets;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class DiscoverActivity extends ParentActivity {

    private static final String LOG_TAG = DiscoverActivity.class.getSimpleName();

    enum DiscoveryCategory {
        mostPopular(R.string.most_popular_category, R.id.discover_most_popular_menu_item),
        highestRated(R.string.highest_rated_category, R.id.discover_highest_rated_menu_item),
        highestGrossing(
                R.string.highest_grossing_category,
                R.id.discover_highest_grossing_menu_item),
        ;

        private int labelResId;
        private int menuResId;

        DiscoveryCategory(int labelResId, int menuResId) {
            this.labelResId = labelResId;
            this.menuResId = menuResId;
        }

        public int getLabelResId() {
            return labelResId;
        }

        public int getMenuResId() {
            return menuResId;
        }
    }

    public static final String DISCOVER_CATEGORY_EXTRA_CONSTANT = "discoverCategory";
    public static final int DEFAULT_CATEGORY = R.id.discover_most_popular_menu_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DiscoveryCategory discoveryCategory = ActivityNavigator.getDiscoveryCategory(getIntent());
        addMainFragment(DiscoverFragment.newInstance(discoveryCategory));

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
