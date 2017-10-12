package com.playposse.udacitymovie.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.playposse.udacitymovie.data.MovieContentContract.DiscoverListMovieTable;
import com.playposse.udacitymovie.data.MovieContentContract.DiscoveryListTable;
import com.playposse.udacitymovie.data.MovieContentContract.FavoriteTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieReviewTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieVideoTable;

/**
 * A utility for querying the content repository.
 */
public final class ContentProviderQueries {

    private static final String LOG_TAG = ContentProviderQueries.class.getSimpleName();

    private ContentProviderQueries() {
    }

    public static Cursor getDiscoveryLists(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.query(
                DiscoveryListTable.CONTENT_URI,
                DiscoveryListTable.COLUMN_NAMES,
                null,
                null,
                null);
    }

    public static int updateDiscoveryListRetrievalDate(Context context, long discoveryListId) {
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(DiscoveryListTable.LAST_RETRIEVED_COLUMN, System.currentTimeMillis());

        return contentResolver.update(
                DiscoveryListTable.CONTENT_URI,
                values,
                "_id=?",
                new String[]{Long.toString(discoveryListId)});
    }

    public static int deleteDiscoveryListMovieLinks(Context context, long discoveryListId) {
        ContentResolver contentResolver = context.getContentResolver();

        return contentResolver.delete(
                DiscoverListMovieTable.CONTENT_URI,
                DiscoverListMovieTable.DISCOVERY_LIST_ID_COLUMN + "=?",
                new String[]{Long.toString(discoveryListId)});
    }

    public static void insertDiscoveryListMovieLink(
            Context context,
            long discoveryListId,
            long movieId) {

        ContentResolver contentResolver = context.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(DiscoverListMovieTable.DISCOVERY_LIST_ID_COLUMN, discoveryListId);
        values.put(DiscoverListMovieTable.MOVIE_ID_COLUMN, movieId);

        contentResolver.insert(DiscoverListMovieTable.CONTENT_URI, values);
    }

    public static boolean doesMovieExist(Context context, long movieId) {
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(
                MovieTable.CONTENT_URI,
                null,
                MovieTable.ID_COLUMN + "=?",
                new String[]{Long.toString(movieId)},
                null);

        if (cursor == null) {
            Log.e(LOG_TAG, "doesMovieExist: The cursor was unexpectedly null!");
            return false;
        }

        try {
            return cursor.getCount() > 0;
        } finally {
            cursor.close();
        }
    }

    public static boolean doesMovieHaveExtendedInfo(Context context, long movieId) {
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(
                MovieTable.CONTENT_URI,
                null,
                MovieTable.ID_COLUMN + "=? and has_extended_info <> 0",
                new String[]{Long.toString(movieId)},
                null);

        if (cursor == null) {
            Log.e(LOG_TAG, "doesMovieHaveExtendedInfo: The cursor was unexpectedly null!");
            return false;
        }

        try {
            return cursor.getCount() > 0;
        } finally {
            cursor.close();
        }
    }

    public static void insertMovie(Context context, ContentValues values) {
        ContentResolver contentResolver = context.getContentResolver();

        contentResolver.insert(MovieTable.CONTENT_URI, values);
    }

    public static void updateMovie(Context context, long movieId, ContentValues values) {
        ContentResolver contentResolver = context.getContentResolver();

        contentResolver.update(
                MovieTable.CONTENT_URI,
                values,
                MovieTable.ID_COLUMN + "=?",
                new String[]{Long.toString(movieId)});
    }

    public static int deleteMovieReviews(Context context, long movieId) {
        ContentResolver contentResolver = context.getContentResolver();

        return contentResolver.delete(
                MovieReviewTable.CONTENT_URI,
                MovieReviewTable.MOVIE_ID_COLUMN + "=?",
                new String[]{Long.toString(movieId)});
    }

    public static void insertMovieReview(Context context, ContentValues values) {
        ContentResolver contentResolver = context.getContentResolver();

        contentResolver.insert(MovieReviewTable.CONTENT_URI, values);
    }

    public static int deleteMovieVideos(Context context, long movieId) {
        ContentResolver contentResolver = context.getContentResolver();

        return contentResolver.delete(
                MovieVideoTable.CONTENT_URI,
                MovieVideoTable.MOVIE_ID_COLUMN + "=?",
                new String[]{Long.toString(movieId)});
    }

    public static void insertMovieVideo(Context context, ContentValues values) {
        ContentResolver contentResolver = context.getContentResolver();

        contentResolver.insert(MovieVideoTable.CONTENT_URI, values);
    }

    public static Cursor getMoviesWithoutExtendedInfo(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        return contentResolver.query(
                MovieTable.CONTENT_URI,
                MovieTable.COLUMN_NAMES,
                "has_extended_info = 0",
                null,
                null);
    }

    public static void favorMovie(Context context, long movieId, boolean shouldBeFavorite) {
        ContentResolver contentResolver = context.getContentResolver();

        if (shouldBeFavorite) {
            ContentValues values = new ContentValues();
            values.put(FavoriteTable.MOVIE_ID_COLUMN, movieId);

            contentResolver.insert(FavoriteTable.CONTENT_URI, values);
        } else {
            contentResolver.delete(
                    FavoriteTable.CONTENT_URI,
                    FavoriteTable.MOVIE_ID_COLUMN + "=?",
                    new String[]{Long.toString(movieId)});
        }
    }

    public static boolean areDiscoveryListsCached(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(
                DiscoveryListTable.CONTENT_URI,
                null,
                "last_retrieved is null",
                null,
                null);

        if (cursor == null) {
            Log.e(LOG_TAG, "areDiscoveryListsCached: The cursor was unexpectedly null!");
            return false;
        }

        try {
            return cursor.getCount() == 0;
        } finally {
            cursor.close();
        }
    }
}
