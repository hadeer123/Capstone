package com.smovies.hk.searchmovies.movieSorting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.smovies.hk.searchmovies.BuildConfig;
import com.smovies.hk.searchmovies.data.LoadDBManager;
import com.smovies.hk.searchmovies.model.Movie;
import com.smovies.hk.searchmovies.model.MovieListResponse;
import com.smovies.hk.searchmovies.network.ApiClient;
import com.smovies.hk.searchmovies.network.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smovies.hk.searchmovies.utils.Constants.GET_MOVIES;
import static com.smovies.hk.searchmovies.utils.Constants.PLAYING_NOW;
import static com.smovies.hk.searchmovies.utils.Constants.POPULAR;
import static com.smovies.hk.searchmovies.utils.Constants.TOP_RATED;

public class MovieListModel implements MovieListContract.Model {

    private final String TAG = MovieListModel.class.getSimpleName();

    @Override
    public void getMovieList(final OnFinishedListener onFinishedListener, String query, int pageNo, int tabNumber) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MovieListResponse> call = getMovieListResponseCall(pageNo, query, tabNumber, apiService);

        call.enqueue(new Callback<MovieListResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieListResponse> call, @NonNull Response<MovieListResponse> response) {
                List<Movie> movies = null;
                if (response.body() != null) {
                    movies = response.body().getResults();
                }
                assert movies != null;
                Log.d(TAG, "Number of movies received: " + movies.size());
                onFinishedListener.onFinished(movies);
            }

            @Override
            public void onFailure(@NonNull Call<MovieListResponse> call, @NonNull Throwable t) {
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

    public Call<MovieListResponse> getMovieListResponseCall(int pageNo, String query, int tabNumber, ApiInterface apiService) {
        Call<MovieListResponse> call = null;
        switch (tabNumber) {
            case PLAYING_NOW:
                call =
                        apiService.getNowPlayingMoviesList(BuildConfig.API_KEY, pageNo);
                break;
            case POPULAR:
                call =
                        apiService.getPopularMoviesList(BuildConfig.API_KEY, pageNo);
                break;
            case TOP_RATED:
                call =
                        apiService.getTopRatedMoviesList(BuildConfig.API_KEY, pageNo);
                break;
            case GET_MOVIES:
                call =
                        apiService.getMoviesList(BuildConfig.API_KEY, query, pageNo);
                break;
            default:
                break;
        }
        return call;
    }
}
