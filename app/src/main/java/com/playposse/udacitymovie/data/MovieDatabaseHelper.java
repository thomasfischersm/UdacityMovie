package com.playposse.udacitymovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A helper class that manages the SQLLite database.
 */
public class MovieDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "udacitymovie";
    private static final int DB_VERSION = 1;

    public MovieDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MovieContentContract.MovieTable.SQL_CREATE_TABLE);
        db.execSQL(MovieContentContract.DiscoverListTable.SQL_CREATE_TABLE);
        db.execSQL(MovieContentContract.DiscoverListMovieTable.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
