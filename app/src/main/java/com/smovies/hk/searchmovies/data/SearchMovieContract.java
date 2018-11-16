package com.smovies.hk.searchmovies.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class SearchMovieContract {
    public static final String AUTHORITY = "com.smovies.hk.searchmovies.provider";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    public static final String PATH_MOVIES = "movies";


    public static final class searchMoviesEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();


        // Task table and column names
        public static final String TABLE_NAME = "favMovies";

        public static final String COLUMN_MOVIE_ID = "movieID";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_SAVE_TO_FAV= "IsFav";
        public static final String COLUMN_SAVE_TO_WATCH= "IsToWatch";
    }
}
