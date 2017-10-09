package com.playposse.udacitymovie.util;

import android.database.sqlite.SQLiteDatabase;

/**
 * A utility for dealing with the SQLLite database.
 */
public final class DbUtil {

    private DbUtil() {}

    /**
     * Executes a semicolon delimited list of SQL statements.
     */
    public static void executeMultipleSql(SQLiteDatabase db, String sql) {
        for (String statement : sql.split(";")) {
            db.execSQL(statement);
        }
    }
}
