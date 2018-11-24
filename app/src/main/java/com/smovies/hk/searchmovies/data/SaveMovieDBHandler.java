package com.smovies.hk.searchmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.model.Movie;

public class SaveMovieDBHandler {

    public static final Uri FAV_URI = SearchMovieContract.searchMoviesEntry.CONTENT_URI_FAV;
    public static final Uri TO_WATCH_URI = SearchMovieContract.searchMoviesEntry.CONTENT_URI_TO_WATCH;

    public void ivDBOnClickHandler(Movie movie, Uri contentUri, ImageView imageView, FloatingActionButton floatingActionButton, MenuItem menuItem, Context mContext) {
        if (!isSaved(movie.getId(), contentUri, mContext))
            saveToDB(imageView, floatingActionButton, menuItem, movie
                    , contentUri, mContext);
        else
            unSave(imageView, floatingActionButton, menuItem, movie.getId()
                    , contentUri, mContext);
    }

    public boolean isSaved(int movieID, Uri uri, Context mContext) {
        String stringId = Integer.toString(movieID);
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

    private static SaveMovieDBHandler saveMovieDBHandler = new SaveMovieDBHandler();

    public static SaveMovieDBHandler singleton() {
        return saveMovieDBHandler;
    }

    private void unSave(ImageView imageView, FloatingActionButton floatingActionButton, MenuItem menuItem, int movieID, Uri uri, Context mContext) {
        contentProviderDeleteEntry(movieID, uri, mContext);
        updateViewAfterAction(imageView, floatingActionButton, menuItem, movieID, uri, mContext);
    }

    public void contentProviderUpdateEntry(Movie movie, Uri uri, ContentValues values, Context mContext) {
        int id = movie.getId();
        String stringId = Integer.toString(id);
        uri = uri.buildUpon().appendPath(stringId).build();
        try {
            mContext.getContentResolver().update(uri, values, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void contentProviderDeleteEntry(int movieID, Uri uri, Context mContext) {
        String stringId = Integer.toString(movieID);
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

    public void updateViewAfterAction(ImageView imageView, FloatingActionButton floatingActionButton, MenuItem menuItem, int movieID, Uri uri, Context mContext) {

        if (imageView == null) {
            if (uri.equals(FAV_URI))
                updateSaved(movieID, floatingActionButton, mContext, FAV.SAVE.DEFAULT_VALUE_ID, FAV.UNSAVE.DEFAULT_VALUE_ID, FAV_URI);
        } else if (menuItem == null) {
            if (uri.equals(FAV_URI))
                updateSaved(movieID, imageView, mContext, FAV.SAVE.DEFAULT_VALUE_ID, FAV.UNSAVE.DEFAULT_VALUE_ID, FAV_URI);
            else
                updateSaved(movieID, imageView, mContext, TO_WATCH.SAVE.DEFAULT_VALUE_ID, TO_WATCH.UNSAVE.DEFAULT_VALUE_ID, TO_WATCH_URI);
        } else
            updateSaved(movieID, menuItem, mContext, TO_WATCH.SAVE.DEFAULT_VALUE_ID, TO_WATCH.UNSAVE.DEFAULT_VALUE_ID, TO_WATCH_URI);
    }

    public void saveToDB(ImageView imageView, FloatingActionButton floatingActionButton, MenuItem menuItem, Movie movie, Uri path, Context mContext) {

        ContentValues contentValues = getMovieContentValues(movie);

        Uri uri = mContext.getContentResolver().insert(path, contentValues);

        if (uri != null) {
            Toast.makeText(mContext, uri.toString() + mContext.getString(R.string.saved_msg), Toast.LENGTH_SHORT).show();
            updateViewAfterAction(imageView, floatingActionButton, menuItem, movie.getId(), path, mContext);
        }
    }

    public int updateSavedResource(int movieID, Context mContext, int savedId, int unsavedId, Uri uri) {
        int imgResFav = isSaved(movieID, uri, mContext) ?
                savedId : unsavedId;
        return imgResFav;
    }

    public void updateSaved(int movieID, ImageView imageView, Context mContext, int savedId, int unsavedId, Uri uri) {
        imageView.setImageResource(updateSavedResource(movieID, mContext, savedId, unsavedId, uri));
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

    public void updateSaved(int movieID, FloatingActionButton floatingActionButton, Context mContext, int savedId, int unsavedId, Uri uri) {
        floatingActionButton.setImageResource(updateSavedResource(movieID, mContext, savedId, unsavedId, uri));
    }

    public void updateSaved(int movieID, MenuItem menuItem, Context mContext, int savedId, int unsavedId, Uri uri) {
        menuItem.setIcon(updateSavedResource(movieID, mContext, savedId, unsavedId, uri));
    }

    public enum FAV {
        SAVE(R.mipmap.ic_fav_full),
        UNSAVE(R.mipmap.ic_fav_empty);
        public final int DEFAULT_VALUE_ID;

        FAV(int default_value_id) {
            DEFAULT_VALUE_ID = default_value_id;
        }
    }

    public enum TO_WATCH {
        SAVE(R.mipmap.ic_plus_full),
        UNSAVE(R.mipmap.ic_plus_empty);

        public final int DEFAULT_VALUE_ID;

        TO_WATCH(int default_value_id) {
            DEFAULT_VALUE_ID = default_value_id;
        }
    }

}
