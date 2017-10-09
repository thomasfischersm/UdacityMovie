package com.playposse.udacitymovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.playposse.udacitymovie.data.MovieContentContract.DiscoverListMovieTable;
import com.playposse.udacitymovie.data.MovieContentContract.DiscoveryListTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieTable;
import com.playposse.udacitymovie.util.DbUtil;

/**
 * A helper class that manages the SQLLite database.
 */
public class MovieDatabaseHelper extends SQLiteOpenHelper {

    public  static final String DB_NAME = "udacitymovie";

    private static final int DB_VERSION = 1;

    public MovieDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MovieTable.SQL_CREATE_TABLE);
        db.execSQL(DiscoveryListTable.SQL_CREATE_TABLE);
        db.execSQL(DiscoverListMovieTable.SQL_CREATE_TABLE);

        DbUtil.executeMultipleSql(db, DiscoveryListTable.SQL_CREATE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
