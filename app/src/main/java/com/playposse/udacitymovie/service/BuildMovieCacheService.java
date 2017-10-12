package com.playposse.udacitymovie.service;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.playposse.udacitymovie.BuildConfig;
import com.playposse.udacitymovie.data.ContentProviderQueries;
import com.playposse.udacitymovie.data.MovieContentContract.DiscoveryListTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieReviewTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieVideoTable;
import com.playposse.udacitymovie.data.MovieDatabaseHelper;
import com.playposse.udacitymovie.util.DatabaseDumper;
import com.playposse.udacitymovie.util.NetworkUtil;
import com.playposse.udacitymovie.util.SmartCursor;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Reviews;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * A {@link Service} that queries the Movie DB API to cache all useful data locally.
 *
 * <p>There are different ways to invoke this service:
 * <ul>
 *     <li>No parameter means to fill the basic cache.</li>
 *     <li>Specify a movieId and the extended information for that movie is loaded.</li>
 * </ul>
 * 
 * <p>The initial cache call only loads the extended movie info on WiFi.
 */
public class BuildMovieCacheService extends IntentService {

    /**
     * Intent parameter to specify for a specific movieId. The extended info for that movie will
     * be loaded.
     */
    public static final String MOVIE_ID_KEY = "movieId";

    private static final String LOG_TAG = BuildMovieCacheService.class.getSimpleName();

    private static final String SERVICE_NAME = "BuildMovieCacheService";

    public BuildMovieCacheService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        long start = System.currentTimeMillis();
        Context context = getApplicationContext();

        long movieId = (intent != null) ? intent.getLongExtra(MOVIE_ID_KEY, -1) : -1;
        if (movieId != -1) {
            queryExtendedMovieInfo(movieId);
        } else {
            buildCache();
        }

        long end = System.currentTimeMillis();
        Log.i(LOG_TAG, "onHandleIntent: Fetching movie data took " + (end - start) + "ms.");

        // Dump the database to the log.
        if (BuildConfig.DEBUG) {
            MovieDatabaseHelper databaseHelper = new MovieDatabaseHelper(context);
            DatabaseDumper.dumpTables(databaseHelper);
            databaseHelper.close();
        }
    }

    private void buildCache() {
        // Check if the cache has already been built.
        if (ContentProviderQueries.areDiscoveryListsCached(getApplicationContext())) {
            Log.i(LOG_TAG, "buildCache: Skip cache building because the cache is already built.");
            return;
        }

        queryAllDiscoveryLists();

        if (NetworkUtil.isWifiActive(getApplicationContext())) {
            Log.i(LOG_TAG, "buildCache: Not on Wifi! Skipping query for extended movie info.");
            queryExtendedMovieInfo();
        }
    }

    private void queryAllDiscoveryLists() {
        Context context = getApplicationContext();
        Cursor discoveryLists = ContentProviderQueries.getDiscoveryLists(context);
        SmartCursor smartCursor = new SmartCursor(discoveryLists, DiscoveryListTable.COLUMN_NAMES);

        try {
            while (discoveryLists.moveToNext()) {
                long discoveryListId = smartCursor.getLong(DiscoveryListTable.ID_COLUMN);
                String sortBy = smartCursor.getString(DiscoveryListTable.SORT_BY_FILTER_COLUMN);
                queryDiscoveryList(context, discoveryListId, sortBy);
            }
        } finally {
            discoveryLists.close();
        }
    }

    private void queryDiscoveryList(Context context, long discoveryListId, String sortBy) {
        // Update last retrieved timestamp.
        ContentProviderQueries.updateDiscoveryListRetrievalDate(context, discoveryListId);

        // Clear old link entries
        int deleteCount = ContentProviderQueries.deleteDiscoveryListMovieLinks(
                context,
                discoveryListId);
        Log.i(LOG_TAG, "onHandleIntent: Deleted link rows: " + deleteCount);

        // Create link entries and movies
        MovieResultsPage resultsPage = MovieDbApiQueries.getDiscoveryList(sortBy);
        for (MovieDb movie : resultsPage.getResults()) {
            int movieId = movie.getId();

            // Create link entry.
            ContentProviderQueries.insertDiscoveryListMovieLink(
                    context,
                    discoveryListId,
                    movieId);

            // Update or insert the movie data.
            insertOrUpdateMovie(movie, false);
        }
    }

    private void insertOrUpdateMovie(MovieDb movie, boolean hasExtendedInfo) {
        Context context = getApplicationContext();
        int movieId = movie.getId();

        ContentValues values = new ContentValues();
        values.put(MovieTable.ID_COLUMN, movieId);
        values.put(MovieTable.IMDB_ID_COLUMN, movie.getImdbID());
        values.put(MovieTable.TITLE_COLUMN, movie.getTitle());
        values.put(MovieTable.TAGLINE_COLUMN, movie.getTagline());
        values.put(MovieTable.USER_RATING_COLUMN, movie.getUserRating());
        values.put(MovieTable.VOTE_AVERAGE_COLUMN, movie.getVoteAverage());
        values.put(MovieTable.OVERVIEW_COLUMN, movie.getOverview());
        values.put(MovieTable.POSTER_PATH_COLUMN, movie.getPosterPath());
        values.put(MovieTable.BACKDROP_PATH_COLUMN, movie.getBackdropPath());
        values.put(MovieTable.RUNTIME_COLUMN, movie.getRuntime());

        String releaseDate = movie.getReleaseDate();
        if ((releaseDate != null) && (releaseDate.length() >= 4)) {
            String releaseYear = releaseDate.substring(0, 4);
            values.put(MovieTable.RELEASE_YEAR_COLUMN, releaseYear);
        }

        if (hasExtendedInfo) {
            values.put(MovieTable.HAS_EXTENDED_INFO_COLUMN, true);
        }

        // Clear empty fields if the query has no extended info. The null values could overwrite
        // extended info from a previous query.
        if (!hasExtendedInfo) {
            // Prevent concurrent modification exception.
            List<String> keys = new ArrayList<>(values.keySet());
            for (String key : keys) {
                if (values.get(key) == null) {
                    values.remove(key);
                }
            }
        }

        if (ContentProviderQueries.doesMovieExist(context, movieId)) {
            ContentProviderQueries.updateMovie(context, movieId, values);
        } else {
            ContentProviderQueries.insertMovie(context, values);
        }

        if (hasExtendedInfo) {
            recreateMovieReviews(movie);
            recreateMovieVideos(movie);
        }
    }

    private void recreateMovieReviews(MovieDb movie) {
        Context context = getApplicationContext();
        int movieId = movie.getId();

        // Delete movie reviews.
        ContentProviderQueries.deleteMovieReviews(context, movieId);

        // Re-insert movie reviews.
        if (movie.getReviews() != null) {
            for (Reviews review : movie.getReviews()) {
                ContentValues values = new ContentValues();
                values.put(MovieReviewTable.MOVIE_ID_COLUMN, movieId);
                values.put(MovieReviewTable.AUTHOR_COLUMN, review.getAuthor());
                values.put(MovieReviewTable.CONTENT_COLUMN, review.getContent());
                values.put(MovieReviewTable.URL_COLUMN, review.getUrl());

                ContentProviderQueries.insertMovieReview(context, values);
            }
        }
    }

    private void recreateMovieVideos(MovieDb movie) {
        Context context = getApplicationContext();
        int movieId = movie.getId();

        // Delete movie reviews.
        ContentProviderQueries.deleteMovieVideos(context, movieId);

        // Re-insert movie reviews.
        if (movie.getReviews() != null) {
            for (Video video : movie.getVideos()) {
                ContentValues values = new ContentValues();
                values.put(MovieVideoTable.MOVIE_ID_COLUMN, movieId);
                values.put(MovieVideoTable.TYPE_COLUMN, video.getType());
                values.put(MovieVideoTable.KEY_COLUMN, video.getKey());
                values.put(MovieVideoTable.SITE_COLUMN, video.getSite());

                ContentProviderQueries.insertMovieVideo(context, values);
            }
        }
    }

    private void queryExtendedMovieInfo() {
        Context context = getApplicationContext();

        Cursor cursor = ContentProviderQueries.getMoviesWithoutExtendedInfo(context);
        SmartCursor smartCursor = new SmartCursor(cursor, MovieTable.COLUMN_NAMES);
        int count = cursor.getCount();

        try {
            while (cursor.moveToNext()) {
                long movieId = smartCursor.getLong(MovieTable.ID_COLUMN);
                MovieDb movie = MovieDbApiQueries.getExtendedMovieInfo(movieId);
                insertOrUpdateMovie(movie, true);
            }
        } finally {
            cursor.close();
        }

        Log.i(LOG_TAG, "queryExtendedMovieInfo: Got extended movie info for " + count + " movies.");
    }

    private void queryExtendedMovieInfo(long movieId) {
        if (!ContentProviderQueries.doesMovieHaveExtendedInfo(getApplicationContext(), movieId)) {
            MovieDb movie = MovieDbApiQueries.getExtendedMovieInfo(movieId);
            insertOrUpdateMovie(movie, true);
        }
    }
}
