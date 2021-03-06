package com.smovies.hk.searchmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SavedMovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "searchMovies.db";
    private static final int VERSION = 1;

    SavedMovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getDBTable(SearchMovieContract.searchMoviesEntry.FAV_TABLE_NAME));
        db.execSQL(getDBTable(SearchMovieContract.searchMoviesEntry.TO_WATCH_TABLE_NAME));
    }

    private String getDBTable(String TABLE_NAME) {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                SearchMovieContract.searchMoviesEntry._ID + " INTEGER PRIMARY KEY, " +
                SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL," +
                SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT," +
                SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RATING + " FLOAT," +
                SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_THUMB_PATH + " TEXT," +
                SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT," +
                SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT," +
                SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RUNTIME + " TEXT," +
                SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_FAV + " BOOLEAN NOT NULL DEFAULT 0," +
                SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_WATCH + " BOOLEAN NOT NULL DEFAULT 0 );";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO do something here ??
        db.execSQL("DROP TABLE IF EXISTS " + SearchMovieContract.searchMoviesEntry.FAV_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SearchMovieContract.searchMoviesEntry.TO_WATCH_TABLE_NAME);
    }

}
