package com.smovies.hk.searchmovies.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class SearchMovieContract {
    public static final String AUTHORITY = "com.smovies.hk.searchmovies.provider";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    public static final String PATH_MOVIES = "movies";
    public static final String PATH_TO_FAV = "toFav";
    public static final String PATH_TO_WATCH = "toWatch";

    public static final class searchMoviesEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final Uri CONTENT_URI_FAV =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).appendPath(PATH_TO_FAV).build();

        public static final Uri CONTENT_URI_TO_WATCH =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).appendPath(PATH_TO_WATCH).build();

        // Task table and column names
        public static final String FAV_TABLE_NAME = "favMovies";
        public static final String TO_WATCH_TABLE_NAME = "toWatch";

        public static final String COLUMN_MOVIE_ID = "movieID";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_SAVE_TO_FAV = "IsFav";
        public static final String COLUMN_SAVE_TO_WATCH = "IsToWatch";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_THUMB_PATH = "thumbPath";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "backdropPath";
        public static final String COLUMN_MOVIE_CREDITS = "credits";
        public static final String COLUMN_MOVIE_RUNTIME = "runtime";
        public static final String COLUMN_MOVIE_TAGLINE = "tagline";
        public static final String COLUMN_MOVIE_HOMEPAGE = "homepage";
    }
}
