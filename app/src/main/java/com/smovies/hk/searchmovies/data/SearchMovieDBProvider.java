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
    private static final String TAG = SearchMovieDBProvider.class.getSimpleName();
    private static final int ARGS_SECOND_INDEX = 2;
    private static final String WHERE = "%s=?";

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
        Cursor cursor = null;
        String id = "";
        String dbTable = "";
        switch (match){
            case IN_FAV:
                dbTable = SearchMovieContract.searchMoviesEntry.FAV_TABLE_NAME;
                break;
            case IN_TO_WATCH:
                dbTable = SearchMovieContract.searchMoviesEntry.TO_WATCH_TABLE_NAME;
                break;
            case IN_FAV_WITH_ID:
                id = uri.getPathSegments().get(ARGS_SECOND_INDEX);
                dbTable = SearchMovieContract.searchMoviesEntry.FAV_TABLE_NAME;
                selection = String.format(WHERE, SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID);
                selectionArgs = new String[]{id};
                break;
            case IN_TO_WATCH_WITH_ID:
                id = uri.getPathSegments().get(ARGS_SECOND_INDEX);
                dbTable = SearchMovieContract.searchMoviesEntry.TO_WATCH_TABLE_NAME;
                selection = String.format(WHERE, SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID);
                selectionArgs = new String[]{id};
                break;
            case MOVIES_WITH_ID:
                break;
            default:
                unsupportedSchemeException(uri);
        }

        try {
            cursor = db.query(dbTable, projection, selection, selectionArgs, null, null, sortOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        cursor.notifyAll();
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
//                returnUri = insertInDB(uri, values, db, SearchMovieContract.searchMoviesEntry.CONTENT_URI);
                break;
            case IN_FAV:
                returnUri = insertInDB(uri, values, db, SearchMovieContract.searchMoviesEntry.CONTENT_URI_FAV,
                        SearchMovieContract.searchMoviesEntry.FAV_TABLE_NAME);
                break;
            case IN_TO_WATCH:
                returnUri = insertInDB(uri, values, db, SearchMovieContract.searchMoviesEntry.CONTENT_URI_TO_WATCH,
                        SearchMovieContract.searchMoviesEntry.TO_WATCH_TABLE_NAME);
                break;
            default:
                unsupportedSchemeException(uri);
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        notifyAll();
        return returnUri;
    }

    private void unsupportedSchemeException(@NonNull Uri uri) {
        try {
            throw new UnsupportedSchemeException("Unknown uri : " + uri);
        } catch (UnsupportedSchemeException e) {
            e.printStackTrace();
        }
    }

    private Uri insertInDB(@NonNull Uri uri, ContentValues values, SQLiteDatabase db, Uri contentUri, String tableName) {
        long id;
        Uri returnUri = null;
        id = db.insert(tableName, null, values);
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
        String tableName = "";
        int movieDeleted = 0;
        String id = "";

        switch (match){
            case  MOVIES_WITH_ID:
                id = uri.getPathSegments().get(1);
                break;
            case IN_FAV_WITH_ID:
                id = uri.getPathSegments().get(ARGS_SECOND_INDEX);
                tableName = SearchMovieContract.searchMoviesEntry.FAV_TABLE_NAME;
                selection = String.format(WHERE, SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID);
                selectionArgs = new String[]{id};
                break;
            case IN_TO_WATCH_WITH_ID:
                id = uri.getPathSegments().get(ARGS_SECOND_INDEX);
                tableName = SearchMovieContract.searchMoviesEntry.TO_WATCH_TABLE_NAME;
                selection = String.format(WHERE, SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID);
                selectionArgs = new String[]{id};
                break;
            default:
                unsupportedSchemeException(uri);

        }

        try {
            movieDeleted = db.delete(tableName,
                    selection, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(movieDeleted !=0)
            getContext().getContentResolver().notifyChange(uri, null);
        notifyAll();
        return movieDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = favoriteMovieDBHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        int movieUpdated = 0;
        String tableName = "";
        String id;

        switch (match) {
            case IN_FAV_WITH_ID:
                id = uri.getPathSegments().get(ARGS_SECOND_INDEX);
                tableName = SearchMovieContract.searchMoviesEntry.FAV_TABLE_NAME;
                selection = String.format(WHERE, SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID);
                selectionArgs = new String[]{id};
                break;
            case IN_TO_WATCH_WITH_ID:
                id = uri.getPathSegments().get(ARGS_SECOND_INDEX);
                tableName = SearchMovieContract.searchMoviesEntry.TO_WATCH_TABLE_NAME;
                selection = String.format(WHERE, SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID);
                selectionArgs = new String[]{id};
                break;
            default:
                unsupportedSchemeException(uri);

        }

        try {
            movieUpdated = db.update(tableName, values, selection, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (movieUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        notifyAll();
        return movieUpdated;
    }
}
