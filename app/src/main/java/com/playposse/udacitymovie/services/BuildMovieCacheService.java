package com.playposse.udacitymovie.services;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.playposse.udacitymovie.data.ContentProviderQueries;
import com.playposse.udacitymovie.data.MovieContentContract.DiscoveryListTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieTable;
import com.playposse.udacitymovie.data.MovieDatabaseHelper;
import com.playposse.udacitymovie.util.DatabaseDumper;
import com.playposse.udacitymovie.util.SmartCursor;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * A {@link Service} that queries the Movie DB API to cache all useful data locally.
 */
public class BuildMovieCacheService extends IntentService {

    private static final String LOG_TAG = BuildMovieCacheService.class.getSimpleName();

    private static final String SERVICE_NAME = "BuildMovieCacheService";

    public BuildMovieCacheService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        long start = System.currentTimeMillis();
        Context context = getApplicationContext();
        Cursor discoveryLists = ContentProviderQueries.getDiscoveryLists(context);
        SmartCursor smartCursor = new SmartCursor(discoveryLists, DiscoveryListTable.COLUMN_NAMES);

        try {
            while (discoveryLists.moveToNext()) {
                long discoveryListId = smartCursor.getLong(DiscoveryListTable.ID_COLUMN);
                String sortBy = smartCursor.getString(DiscoveryListTable.SORT_BY_FILTER_COLUMN);

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
                    insertOrUpdateMovie(movie);
                }
            }
        } finally {
            discoveryLists.close();
        }

        long end = System.currentTimeMillis();
        Log.i(LOG_TAG, "onHandleIntent: Fetching movie data took " + (end - start) + "ms.");

        DatabaseDumper.dumpTables(new MovieDatabaseHelper(context));
    }

    private void insertOrUpdateMovie(MovieDb movie) {
        int movieId = movie.getId();
        Context context = getApplicationContext();

        ContentValues values = new ContentValues();
        values.put(MovieTable.ID_COLUMN, movieId);
        values.put(MovieTable.IMDB_ID_COLUMN, movie.getImdbID());
        values.put(MovieTable.TITLE_COLUMN, movie.getTitle());
        values.put(MovieTable.TAGLINE_COLUMN, movie.getTagline());
        values.put(MovieTable.USER_RATING_COLUMN, movie.getUserRating());
        values.put(MovieTable.VOTE_AVERAGE_COLUMN, movie.getVoteAverage());
        values.put(MovieTable.OVERVIEW_COLUMN, movie.getOverview());
        values.put(MovieTable.POSTER_PATH_COLUMN, movie.getPosterPath());

        if (ContentProviderQueries.doesMovieExist(context, movieId)) {
            ContentProviderQueries.updateMovie(context, movieId, values);
        } else {
            ContentProviderQueries.insertMovie(context, values);
        }
    }
}
