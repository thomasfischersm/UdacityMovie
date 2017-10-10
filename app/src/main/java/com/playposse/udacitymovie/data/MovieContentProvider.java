package com.playposse.udacitymovie.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.playposse.udacitymovie.data.MovieContentContract.DiscoverListMovieTable;
import com.playposse.udacitymovie.data.MovieContentContract.DiscoveryCategoryQuery;
import com.playposse.udacitymovie.data.MovieContentContract.DiscoveryListTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieReviewTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieVideoTable;

/**
 * A {@link ContentProvider} that stores the movie information from the Movie Database locally.
 */
public class MovieContentProvider extends ContentProvider {

    private static final String LOG_TAG = MovieContentProvider.class.getSimpleName();

    private static final int MOVIE_TABLE_KEY = 1;
    private static final int DISCOVERY_LIST_TABLE_KEY = 2;
    private static final int DISCOVERY_LIST_MOVIE_TABLE_KEY = 3;
    private static final int MOVIE_REVIEW_TABLE_KEY = 4;
    private static final int MOVIE_VIDEO_TABLE_KEY = 5;
    private static final int DISCOVERY_CATEGORY_QUERY = 6;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Max line limit is intentionally broken because this is much more readable on one line.
        uriMatcher.addURI(MovieContentContract.AUTHORITY, MovieTable.PATH, MOVIE_TABLE_KEY);
        uriMatcher.addURI(MovieContentContract.AUTHORITY, DiscoveryListTable.PATH, DISCOVERY_LIST_TABLE_KEY);
        uriMatcher.addURI(MovieContentContract.AUTHORITY, DiscoverListMovieTable.PATH, DISCOVERY_LIST_MOVIE_TABLE_KEY);
        uriMatcher.addURI(MovieContentContract.AUTHORITY, MovieReviewTable.PATH, MOVIE_REVIEW_TABLE_KEY);
        uriMatcher.addURI(MovieContentContract.AUTHORITY, MovieVideoTable.PATH, MOVIE_VIDEO_TABLE_KEY);
        uriMatcher.addURI(MovieContentContract.AUTHORITY, DiscoveryCategoryQuery.PATH, DISCOVERY_CATEGORY_QUERY);
    }

    private MovieDatabaseHelper databaseHelper;

    @Override
    public boolean onCreate() {
        databaseHelper = new MovieDatabaseHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String sortOrder) {

        ContentResolver contentResolver = getContext().getContentResolver();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String tableName;
        Uri notificationUri = null;
        switch (uriMatcher.match(uri)) {
            case MOVIE_TABLE_KEY:
                tableName = MovieTable.TABLE_NAME;
                notificationUri = MovieTable.CONTENT_URI;
                break;
            case DISCOVERY_LIST_TABLE_KEY:
                tableName = DiscoveryListTable.TABLE_NAME;
                notificationUri = DiscoveryListTable.CONTENT_URI;
                break;
            case DISCOVERY_LIST_MOVIE_TABLE_KEY:
                tableName = DiscoverListMovieTable.TABLE_NAME;
                notificationUri = DiscoverListMovieTable.CONTENT_URI;
                break;
            case MOVIE_REVIEW_TABLE_KEY:
                tableName = MovieReviewTable.TABLE_NAME;
                notificationUri = MovieReviewTable.CONTENT_URI;
                break;
            case MOVIE_VIDEO_TABLE_KEY:
                tableName = MovieVideoTable.TABLE_NAME;
                notificationUri = MovieVideoTable.CONTENT_URI;
                break;
            case DISCOVERY_CATEGORY_QUERY:
                // Expects the selectionArgs to be the ui_label_res_id.
                Cursor discoveryCursor =
                        database.rawQuery(DiscoveryCategoryQuery.SQL_QUERY, selectionArgs);
                discoveryCursor.setNotificationUri(
                        contentResolver,
                        DiscoveryCategoryQuery.CONTENT_URI);
                return discoveryCursor;
            default:
                return null;
        }

        Cursor cursor = database.query(
                tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        if (notificationUri != null) {
            cursor.setNotificationUri(contentResolver, notificationUri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (getContext() == null) {
            Log.e(LOG_TAG, "insert: getContext was null!");
            return null;
        }

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentResolver contentResolver = getContext().getContentResolver();

        switch (uriMatcher.match(uri)) {
            case MOVIE_TABLE_KEY:
                long movieId = database.insert(MovieTable.TABLE_NAME, null, values);
                contentResolver.notifyChange(MovieTable.CONTENT_URI, null);
                contentResolver.notifyChange(DiscoveryCategoryQuery.CONTENT_URI, null);
                return ContentUris.withAppendedId(MovieTable.CONTENT_URI, movieId);
            case DISCOVERY_LIST_TABLE_KEY:
                long discoveryListId = database.insert(DiscoveryListTable.TABLE_NAME, null, values);
                contentResolver.notifyChange(DiscoveryListTable.CONTENT_URI, null);
                contentResolver.notifyChange(DiscoveryCategoryQuery.CONTENT_URI, null);
                return ContentUris.withAppendedId(DiscoveryListTable.CONTENT_URI, discoveryListId);
            case DISCOVERY_LIST_MOVIE_TABLE_KEY:
                long linkId = database.insert(DiscoverListMovieTable.TABLE_NAME, null, values);
                contentResolver.notifyChange(DiscoverListMovieTable.CONTENT_URI, null);
                contentResolver.notifyChange(DiscoveryCategoryQuery.CONTENT_URI, null);
                return ContentUris.withAppendedId(DiscoverListMovieTable.CONTENT_URI, linkId);
            case MOVIE_REVIEW_TABLE_KEY:
                long reviewId = database.insert(MovieReviewTable.TABLE_NAME, null, values);
                contentResolver.notifyChange(MovieReviewTable.CONTENT_URI, null);
                return ContentUris.withAppendedId(MovieReviewTable.CONTENT_URI, reviewId);
            case MOVIE_VIDEO_TABLE_KEY:
                long videoId = database.insert(MovieVideoTable.TABLE_NAME, null, values);
                contentResolver.notifyChange(MovieVideoTable.CONTENT_URI, null);
                return ContentUris.withAppendedId(MovieVideoTable.CONTENT_URI, videoId);
            default:
                return null;
        }
    }

    @Override
    public int update(
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        if (getContext() == null) {
            Log.e(LOG_TAG, "update: Context was unexpectedly null");
            return -1;
        }
        ContentResolver contentResolver = getContext().getContentResolver();

        final int count;
        switch (uriMatcher.match(uri)) {
            case MOVIE_TABLE_KEY:
                count = database.update(MovieTable.TABLE_NAME, values, selection, selectionArgs);
                contentResolver.notifyChange(MovieTable.CONTENT_URI, null);
                contentResolver.notifyChange(DiscoveryCategoryQuery.CONTENT_URI, null);
                break;
            case DISCOVERY_LIST_TABLE_KEY:
                count = database.update(
                        DiscoveryListTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                contentResolver.notifyChange(DiscoveryListTable.CONTENT_URI, null);
                contentResolver.notifyChange(DiscoveryCategoryQuery.CONTENT_URI, null);
                break;
            case DISCOVERY_LIST_MOVIE_TABLE_KEY:
                count = database.update(
                        DiscoverListMovieTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                contentResolver.notifyChange(DiscoverListMovieTable.CONTENT_URI, null);
                contentResolver.notifyChange(DiscoveryCategoryQuery.CONTENT_URI, null);
                break;
            case MOVIE_REVIEW_TABLE_KEY:
                count = database.update(
                        MovieReviewTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                contentResolver.notifyChange(MovieReviewTable.CONTENT_URI, null);
                break;
            case MOVIE_VIDEO_TABLE_KEY:
                count = database.update(
                        MovieVideoTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                contentResolver.notifyChange(MovieVideoTable.CONTENT_URI, null);
                break;
            default:
                return 0;
        }

        return count;
    }

    @Override
    public int delete(
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs) {

        if (getContext() == null) {
            Log.e(LOG_TAG, "delete: getContext was null!");
            return 0;
        }

        ContentResolver contentResolver = getContext().getContentResolver();
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        final int count;
        switch (uriMatcher.match(uri)) {
            case MOVIE_TABLE_KEY:
                count = database.delete(MovieTable.TABLE_NAME, selection, selectionArgs);
                contentResolver.notifyChange(MovieTable.CONTENT_URI, null);
                contentResolver.notifyChange(DiscoveryCategoryQuery.CONTENT_URI, null);
                break;
            case DISCOVERY_LIST_TABLE_KEY:
                count = database.delete(DiscoveryListTable.TABLE_NAME, selection, selectionArgs);
                contentResolver.notifyChange(DiscoveryListTable.CONTENT_URI, null);
                contentResolver.notifyChange(DiscoveryCategoryQuery.CONTENT_URI, null);
                break;
            case DISCOVERY_LIST_MOVIE_TABLE_KEY:
                count = database.delete(
                        DiscoverListMovieTable.TABLE_NAME,
                        selection,
                        selectionArgs);
                contentResolver.notifyChange(DiscoverListMovieTable.CONTENT_URI, null);
                contentResolver.notifyChange(DiscoveryCategoryQuery.CONTENT_URI, null);
                break;
            case MOVIE_REVIEW_TABLE_KEY:
                count = database.delete(MovieReviewTable.TABLE_NAME, selection, selectionArgs);
                contentResolver.notifyChange(MovieReviewTable.CONTENT_URI, null);
                break;
            case MOVIE_VIDEO_TABLE_KEY:
                count = database.delete(MovieVideoTable.TABLE_NAME, selection, selectionArgs);
                contentResolver.notifyChange(MovieVideoTable.CONTENT_URI, null);
                break;
            default:
                return 0;

        }

        return count;
    }
}
