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
    public void getMovieListFromDB(final OnFinishedListener onFinishedListener, final int tabNumber, final Context mContext, MovieListFragment movieListFragment) {

        new LoadDBManager(mContext, movieListFragment, onFinishedListener, tabNumber);

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
}
