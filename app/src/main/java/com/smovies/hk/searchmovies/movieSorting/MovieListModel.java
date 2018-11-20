package com.smovies.hk.searchmovies.movieSorting;

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

import com.smovies.hk.searchmovies.data.SearchMovieContract;
import com.smovies.hk.searchmovies.model.Movie;
import com.smovies.hk.searchmovies.model.MovieListResponse;
import com.smovies.hk.searchmovies.network.ApiClient;
import com.smovies.hk.searchmovies.network.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smovies.hk.searchmovies.network.ApiClient.API_MOVIE_DB;
import static com.smovies.hk.searchmovies.utils.Constants.FAV_LIST;
import static com.smovies.hk.searchmovies.utils.Constants.PLAYING_NOW;
import static com.smovies.hk.searchmovies.utils.Constants.POPULAR;
import static com.smovies.hk.searchmovies.utils.Constants.TOP_RATED;
import static com.smovies.hk.searchmovies.utils.Constants.TO_WATCH_LIST;

public class MovieListModel implements MovieListContract.Model {

    private final String TAG = MovieListModel.class.getSimpleName();

    @Override
    public void getMovieList(final OnFinishedListener onFinishedListener, int pageNo, int tabNumber) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MovieListResponse> call = getMovieListResponseCall(pageNo, tabNumber, apiService);

        call.enqueue(new Callback<MovieListResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieListResponse> call, Response<MovieListResponse> response) {
                List<Movie> movies = null;
                if (response.body() != null) {
                    movies = response.body().getResults();
                }
                Log.d(TAG, "Number of movies received: " + movies.size());
                onFinishedListener.onFinished(movies);
            }

            @Override
            public void onFailure(Call<MovieListResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                onFinishedListener.onFailure(t);
            }
        });
    }

    @Override
    public void getMovieListFromDB(final OnFinishedListener onFinishedListener, final int tabNumber, final Context mContext) {

        new LoaderManager.LoaderCallbacks<Cursor>() {
            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
                LoadDBMovies loadDBMovies = new LoadDBMovies(mContext);
                loadDBMovies.setUri(getUri(tabNumber));
                return loadDBMovies;
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
                if (cursor == null) {
                    onFinishedListener.onFailure(new Throwable("Failed to asynchronously load data"));
                    return;
                }
                List<Movie> movies = fromCursorToMovies(cursor);
                Log.d(TAG, "Number of movies received: " + movies.size());
                onFinishedListener.onFinished(movies);
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {

            }
        };
    }

    public Uri getUri(int tabNumber) {
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
        if (cursor != null && cursor.moveToFirst()) {
            //add row to list
            do {
                movieList.add(getMovieFromCursor(cursor));
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return movieList;
    }

    private Movie getMovieFromCursor(Cursor cursor) {
        //get Columns
        int idC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID);
        int titleC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_TITLE);
        int releaseDateC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RELEASE_DATE);
        int ratingC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RATING);
        int thumbPathC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_THUMB_PATH);
        int overviewC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_OVERVIEW);
        int backdropPathC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_BACKDROP_PATH);
        int runTimeC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RUNTIME);
        int taglineC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_TAGLINE);
        int homepageC = cursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_HOMEPAGE);

        int thisId = cursor.getInt(idC);
        String thisTitle = cursor.getString(titleC);
        String thisReleaseDate = cursor.getString(releaseDateC);
        float thisRatings = cursor.getFloat(ratingC);
        String thisThumbPath = cursor.getString(thumbPathC);
        String thisOverview = cursor.getString(overviewC);
        String thisBackdropPath = cursor.getString(backdropPathC);
        String thisRunTimePath = cursor.getString(runTimeC);
        String thisTagline = cursor.getString(taglineC);
        String thisHomepage = cursor.getString(homepageC);

        return new Movie(thisId, thisTitle, thisReleaseDate, thisRatings, thisThumbPath, thisOverview, thisBackdropPath, thisRunTimePath, thisTagline, thisHomepage);
    }

    public Call<MovieListResponse> getMovieListResponseCall(int pageNo, int tabNumber, ApiInterface apiService) {
        Call<MovieListResponse> call = null;
        switch (tabNumber) {
            case PLAYING_NOW:
                call =
                        apiService.getNowPlayingMoviesList(API_MOVIE_DB, pageNo);
                break;
            case POPULAR:
                call =
                        apiService.getPopularMoviesList(API_MOVIE_DB, pageNo);
                break;
            case TOP_RATED:
                call =
                        apiService.getTopRatedMoviesList(API_MOVIE_DB, pageNo);
                break;
            default:
                break;
        }
        return call;
    }

    public class LoadDBMovies extends AsyncTaskLoader<Cursor> {
        Cursor mFavoriteMoviesData;
        private Uri uri;

        public LoadDBMovies(@NonNull Context context) {
            super(context);
        }

        public void setUri(Uri uri) {
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
                Log.e(TAG, "Failed to asynchronously load data.");
                e.printStackTrace();
                return null;
            }
        }


    }
}
