package com.smovies.hk.searchmovies.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.UnsupportedSchemeException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class SearchMovieDBProvider extends ContentProvider{

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID= 101;

    public static final int IN_FAV = 200;
    public static final int IN_FAV_WITH_ID = 201;

    public static final int IN_TO_WATCH = 300;
    public static final int IN_TO_WATCH_WITH_ID = 301;

    private static final UriMatcher uriMatcher= buildUriMatcher();

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
        String id;
        String[] mSelectionArgs;

        switch (match){
            case MOVIES:
                cursor = db.query(SearchMovieContract.searchMoviesEntry.TABLE_NAME, projection, selection,selectionArgs,null,null,sortOrder);
                break;
            case IN_FAV:
                selection = SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_FAV + "=1";
                cursor = db.query(SearchMovieContract.searchMoviesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case IN_TO_WATCH:
                selection = SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_WATCH + "=1";
                cursor = db.query(SearchMovieContract.searchMoviesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case IN_FAV_WITH_ID:
                id = uri.getPathSegments().get(2);
                selection = SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID + "=?" + " AND "
                        + SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_FAV + "=1";
                mSelectionArgs = new String[]{id};
                cursor = db.query(SearchMovieContract.searchMoviesEntry.TABLE_NAME, projection, selection, mSelectionArgs, null, null, sortOrder);
                break;
            case IN_TO_WATCH_WITH_ID:
                id = uri.getPathSegments().get(2);
                selection = SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID + "=?" + " AND "
                        + SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_WATCH + "=1";
                mSelectionArgs = new String[]{id};
                cursor = db.query(SearchMovieContract.searchMoviesEntry.TABLE_NAME, projection, selection, mSelectionArgs, null, null, sortOrder);
                break;
            case MOVIES_WITH_ID:
                id = uri.getPathSegments().get(1);
                String mSelection = SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID + "=?";
                mSelectionArgs = new String[]{id};
                cursor = db.query(SearchMovieContract.searchMoviesEntry.TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            default:
                try {
                    throw new UnsupportedSchemeException("Unknown uri : "+uri);
                } catch (UnsupportedSchemeException e) {
                    e.printStackTrace();
                }
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
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
                id = db.insert(SearchMovieContract.searchMoviesEntry.TABLE_NAME, null, values);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(SearchMovieContract.searchMoviesEntry.CONTENT_URI, id);
                }else {
                    throw  new android.database.SQLException("Failed to insert row into "+ uri);
                }
                break;
            case IN_FAV:
                id = db.insert(SearchMovieContract.searchMoviesEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(SearchMovieContract.searchMoviesEntry.CONTENT_URI_FAV, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                try {
                    throw new UnsupportedSchemeException("Unknown uri : "+ uri);
                } catch (UnsupportedSchemeException e) {
                    e.printStackTrace();
                }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = favoriteMovieDBHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        int movieDeleted;

        switch (match){
            case  MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                movieDeleted = db.delete(SearchMovieContract.searchMoviesEntry.TABLE_NAME,
                        SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);

        }
        if(movieDeleted !=0)
            getContext().getContentResolver().notifyChange(uri, null);

        return movieDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet Implemented");
    }
}
