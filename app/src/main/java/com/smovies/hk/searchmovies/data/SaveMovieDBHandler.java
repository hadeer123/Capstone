package com.smovies.hk.searchmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.Toast;

import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.model.Movie;

public class SaveMovieDBHandler {

    private static SaveMovieDBHandler saveMovieDBHandler = new SaveMovieDBHandler();

    public static SaveMovieDBHandler singleton() {
        return saveMovieDBHandler;
    }

    public void ivDBOnClickHandler(Movie movie, Uri contentUri, ImageView imageView, Context mContext) {
        if (!isSaved(movie, contentUri, mContext))
            saveToDB(imageView, movie
                    , contentUri, mContext);
        else
            unSave(imageView, movie
                    , contentUri, mContext);
    }

    public boolean isSaved(Movie movie, Uri uri, Context mContext) {
        int id = movie.getId();
        String stringId = Integer.toString(id);
        uri = uri.buildUpon().appendPath(stringId).build();
        Cursor cursor = mContext
                .getContentResolver().query(uri, null, null, null, null, null);

        boolean re = false;
        try {
            re = cursor.getCount() != 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return re;
    }

    private void unSave(ImageView imageView, Movie movie, Uri uri, Context mContext) {
        contentProviderDeleteEntry(movie, uri, mContext);
        updateViewAfterAction(imageView, movie, uri, mContext);
    }

    public void contentProviderUpdateEntry(Movie movie, Uri uri, int bool, ContentValues values, Context mContext) {
        int id = movie.getId();
        String stringId = Integer.toString(id);
        uri = uri.buildUpon().appendPath(stringId).build();
        try {
            mContext.getContentResolver().update(uri, values, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void contentProviderDeleteEntry(Movie movie, Uri uri, Context mContext) {
        int id = movie.getId();
        String stringId = Integer.toString(id);
        uri = uri.buildUpon().appendPath(stringId).build();
        int result = -2;
        try {
            result = mContext.getContentResolver().delete(uri, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result >= 0) {
            Toast.makeText(mContext, mContext.getString(R.string.unsaved_msg), Toast.LENGTH_SHORT).show();
        }

    }

    public void updateViewAfterAction(ImageView imageView, Movie movie, Uri uri, Context mContext) {
        if (uri.equals(SearchMovieContract.searchMoviesEntry.CONTENT_URI_FAV))
            updateSaved(movie, imageView, mContext, R.mipmap.ic_fav_full, R.mipmap.ic_fav_empty);
        else
            updateSaved(movie, imageView, mContext, R.mipmap.ic_plus_full, R.mipmap.ic_plus_empty);
    }

    public void saveToDB(ImageView imageView, Movie movie, Uri path, Context mContext) {

        ContentValues contentValues = getMovieContentValues(movie);

        Uri uri = mContext.getContentResolver().insert(path, contentValues);

        if (uri != null) {
            Toast.makeText(mContext, uri.toString() + mContext.getString(R.string.saved_msg), Toast.LENGTH_SHORT).show();
            updateViewAfterAction(imageView, movie, path, mContext);
        }
    }

    @NonNull
    public ContentValues getMovieContentValues(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        contentValues.put(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RATING, movie.getRating());
        contentValues.put(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_BACKDROP_PATH, movie.getBackdropPath());
        contentValues.put(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_THUMB_PATH, movie.getThumbPath());
        return contentValues;
    }

    public void updateSaved(Movie movie, ImageView imageView, Context mContext, int savedId, int unsavedId) {
        int imgResFav = isSaved(movie, SearchMovieContract.searchMoviesEntry.CONTENT_URI_FAV, mContext) ?
                savedId : unsavedId;
        imageView.setImageResource(imgResFav);
    }

}
