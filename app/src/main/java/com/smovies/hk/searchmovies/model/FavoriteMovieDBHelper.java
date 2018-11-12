package com.smovies.hk.searchmovies.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class FavoriteMovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "faveMovieDb.db";
    private static final int VERSION = 1;

    FavoriteMovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + MovieContract.FavMovieEntry.TABLE_NAME + " (" +
                MovieContract.FavMovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.FavMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.FavMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavMovieEntry.COLUMN_MOVIE_VOTE_AVG+ " DOUBLE NOT NULL, " +
                MovieContract.FavMovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL );";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavMovieEntry.TABLE_NAME);
    }
}
