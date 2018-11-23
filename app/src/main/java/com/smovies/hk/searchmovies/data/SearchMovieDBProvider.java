package com.smovies.hk.searchmovies.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.UnsupportedSchemeException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.smovies.hk.searchmovies.R;

import java.util.Objects;


public class SearchMovieDBProvider extends ContentProvider{

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID= 101;

    public static final int IN_FAV = 200;
    public static final int IN_FAV_WITH_ID = 201;

    public static final int IN_TO_WATCH = 300;
    public static final int IN_TO_WATCH_WITH_ID = 301;

    private static final UriMatcher uriMatcher= buildUriMatcher();
    public static final String IS_TRUE = "=1";
    private static final String TAG = SearchMovieDBProvider.class.getSimpleName();
    private static final String AND = "=? AND ";
    private static final int ARGS_SECOND_INDEX = 2;
    private static final int ARGS_FIRST_INDEX = 1;

    private SavedMovieDBHelper favoriteMovieDBHelper;

    public static UriMatcher buildUriMatcher (){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(SearchMovieContract.AUTHORITY, SearchMovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(SearchMovieContract.AUTHORITY, SearchMovieContract.PATH_MOVIES+"/#",MOVIES_WITH_ID);

        uriMatcher.addURI(SearchMovieContract.AUTHORITY, SearchMovieContract.PATH_MOVIES + "/" + SearchMovieContract.PATH_TO_FAV, IN_FAV);
        uriMatcher.addURI(SearchMovieContract.AUTHORITY, SearchMovieContract.PATH_MOVIES + "/" + SearchMovieContract.PATH_TO_FAV + "/#", IN_FAV_WITH_ID);

        uriMatcher.addURI(SearchMovieContract.AUTHORITY, SearchMovieContract.PATH_MOVIES + "/" + SearchMovieContract.PATH_TO_WATCH, IN_TO_WATCH);
        uriMatcher.addURI(SearchMovieContract.AUTHORITY, SearchMovieContract.PATH_MOVIES + "/" + SearchMovieContract.PATH_TO_WATCH + "/#", IN_TO_WATCH_WITH_ID);

        return uriMatcher;

    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        favoriteMovieDBHelper = new SavedMovieDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = favoriteMovieDBHelper.getReadableDatabase();
        int match = uriMatcher.match(uri);
        Cursor cursor;
        String id;
        switch (match){
            case IN_FAV:
                selection = SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_FAV + IS_TRUE;
                break;
            case IN_TO_WATCH:
                selection = SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_WATCH + IS_TRUE;
                break;
            case IN_FAV_WITH_ID:
                id = uri.getPathSegments().get(ARGS_SECOND_INDEX);
                selection = SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID + AND
                        + SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_FAV + IS_TRUE;
                selectionArgs = new String[]{id};
                break;
            case IN_TO_WATCH_WITH_ID:
                id = uri.getPathSegments().get(ARGS_SECOND_INDEX);
                selection = SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID + AND
                        + SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_WATCH + IS_TRUE;
                selectionArgs = new String[]{id};
                break;
            case MOVIES_WITH_ID:
                id = uri.getPathSegments().get(1);
                selection = SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{id};
                break;

            default:
                unsupportedSchemeException(uri);
        }

        cursor = db.query(SearchMovieContract.searchMoviesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = favoriteMovieDBHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        Uri returnUri = null;
        long id;

        switch (match){
            case MOVIES:
                returnUri = insertInDB(uri, values, db, SearchMovieContract.searchMoviesEntry.CONTENT_URI);
                break;
            case IN_FAV:
                returnUri = insertInDB(uri, values, db, SearchMovieContract.searchMoviesEntry.CONTENT_URI_FAV);
                break;
            case IN_TO_WATCH:
                returnUri = insertInDB(uri, values, db, SearchMovieContract.searchMoviesEntry.CONTENT_URI_TO_WATCH);
                break;
            default:
                unsupportedSchemeException(uri);
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    private void unsupportedSchemeException(@NonNull Uri uri) {
        try {
            throw new UnsupportedSchemeException("Unknown uri : " + uri);
        } catch (UnsupportedSchemeException e) {
            e.printStackTrace();
        }
    }

    private Uri insertInDB(@NonNull Uri uri, ContentValues values, SQLiteDatabase db, Uri contentUri) {
        long id;
        Uri returnUri = null;
        id = db.insert(SearchMovieContract.searchMoviesEntry.TABLE_NAME, null, values);
        if (id > 0) {
            returnUri = ContentUris.withAppendedId(contentUri, id);
        } else {
            sqlError(uri);
        }
        return returnUri;
    }

    private void sqlError(@NonNull Uri uri) {
        try {
            throw new SQLException(getContext().getString(R.string.insert_error) + uri);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = favoriteMovieDBHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        int movieDeleted = 0;

        switch (match){
            case  MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                movieDeleted = db.delete(SearchMovieContract.searchMoviesEntry.TABLE_NAME,
                        SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID + "=?", new String[]{id});
                break;
            default:
                unsupportedSchemeException(uri);

        }
        if(movieDeleted !=0)
            getContext().getContentResolver().notifyChange(uri, null);

        return movieDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = favoriteMovieDBHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int movieUpdated = 0;
        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(ARGS_FIRST_INDEX);
                selection = SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID + AND
                        + SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_FAV + IS_TRUE;
                selectionArgs = new String[]{id};
                break;
            case IN_FAV_WITH_ID:
                id = uri.getPathSegments().get(ARGS_SECOND_INDEX);
                selection = SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID + AND
                        + SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_FAV + IS_TRUE;
                selectionArgs = new String[]{id};
                break;
            case IN_TO_WATCH_WITH_ID:
                id = uri.getPathSegments().get(ARGS_SECOND_INDEX);
                selection = SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID + AND
                        + SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_WATCH + IS_TRUE;
                selectionArgs = new String[]{id};
                break;
            default:
                unsupportedSchemeException(uri);

        }
        if (movieUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        movieUpdated = db.update(SearchMovieContract.searchMoviesEntry.TABLE_NAME, values, selection, selectionArgs);
        return movieUpdated;
    }
}
