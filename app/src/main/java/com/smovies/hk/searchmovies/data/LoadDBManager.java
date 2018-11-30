package com.smovies.hk.searchmovies.data;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.model.Movie;
import com.smovies.hk.searchmovies.movieSorting.MovieListContract;
import com.smovies.hk.searchmovies.movieSorting.MovieListFragment;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static com.smovies.hk.searchmovies.utils.Constants.FAV_LIST;
import static com.smovies.hk.searchmovies.utils.Constants.TO_WATCH_LIST;


public class LoadDBManager implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 55;
    private Context mContext;
    private MovieListContract.Model.OnFinishedListener onFinishedListener;
    private int tabNumber;

    public LoadDBManager(Context mContext, MovieListFragment movieListFragment, MovieListContract.Model.OnFinishedListener onFinishedListener, int tabNumber) {

        this.mContext = mContext;
        this.onFinishedListener = onFinishedListener;
        this.tabNumber = tabNumber;

        movieListFragment.getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new LoadDBMovies(mContext.getApplicationContext(), getUri(tabNumber));
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Cursor cursor) {
        if (cursor == null) {
            onFinishedListener.onFailure(new Throwable("Failed to asynchronously load data"));
            return;
        }
        List<Movie> movies = fromCursorToMovies(cursor);
        Log.d(TAG, mContext.getString(R.string.number_of_movies_msg) + movies.size());
        onFinishedListener.onFinished(movies);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private Uri getUri(int tabNumber) {
        Uri uri = null;
        switch (tabNumber) {
            case FAV_LIST:
                uri = SearchMovieContract.searchMoviesEntry.CONTENT_URI_FAV;
                break;
            case TO_WATCH_LIST:
                uri = SearchMovieContract.searchMoviesEntry.CONTENT_URI_TO_WATCH;
                break;
        }
        return uri;
    }

    private List<Movie> fromCursorToMovies(Cursor cursor) {
        List<Movie> movieList = new ArrayList<>();

        if (cursor != null) {
            try {
                //add row to list
                cursor.moveToFirst();
                do {
                    movieList.add(getMovieFromCursor(cursor));
                } while (cursor.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return movieList;
    }

    public Movie getMovieFromCursor(Cursor cursor) {

        //get Columns
        int idC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID);
        int titleC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_TITLE);
        int releaseDateC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RELEASE_DATE);
        int ratingC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RATING);
        int thumbPathC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_THUMB_PATH);
        int overviewC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_OVERVIEW);
        int backdropPathC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_BACKDROP_PATH);
        int runTimeC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RUNTIME);


        int thisId = cursor.getInt(idC);
        String thisTitle = cursor.getString(titleC);

        String thisReleaseDate = cursor.getString(releaseDateC);
        float thisRatings = cursor.getFloat(ratingC);
        String thisThumbPath = cursor.getString(thumbPathC);
        String thisOverview = cursor.getString(overviewC);
        String thisBackdropPath = cursor.getString(backdropPathC);
        String thisRunTimePath = cursor.getString(runTimeC);


        return new Movie(thisId, thisTitle, thisReleaseDate, thisRatings, thisThumbPath, thisOverview, thisBackdropPath, thisRunTimePath, null, null, null);
    }

    public static class LoadDBMovies extends AsyncTaskLoader<Cursor> {
        private Cursor mFavoriteMoviesData;
        private Uri uri;

        public LoadDBMovies(@NonNull Context context, Uri uri) {
            super(context);
            this.uri = uri;
        }

        @Override
        protected void onStartLoading() {
            if (mFavoriteMoviesData != null) {
                deliverResult(mFavoriteMoviesData);
            } else {
                forceLoad();
            }
        }

        public void deliverResult(Cursor data) {
            mFavoriteMoviesData = data;
            super.deliverResult(data);
        }

        @Nullable
        @Override
        public Cursor loadInBackground() {
            try {
                return getContext().getContentResolver().query(uri,
                        null,
                        null,
                        null,
                        null);

            } catch (Exception e) {
                Log.e(TAG, getContext().getString(R.string.failed_to_load_data));
                e.printStackTrace();
                return null;
            }
        }


    }
}
