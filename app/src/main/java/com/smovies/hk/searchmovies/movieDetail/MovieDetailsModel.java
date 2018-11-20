package com.smovies.hk.searchmovies.movieDetail;

import android.util.Log;

import com.smovies.hk.searchmovies.model.Movie;
import com.smovies.hk.searchmovies.network.ApiClient;
import com.smovies.hk.searchmovies.network.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smovies.hk.searchmovies.network.ApiClient.API_MOVIE_DB;
import static com.smovies.hk.searchmovies.utils.Constants.CREDITS;

public class MovieDetailsModel implements MovieDetailContract.Model {
    private final String TAG = MovieDetailsModel.class.getSimpleName();


    @Override
    public void getMovieDetails(final OnFinishedListener onFinishedListener, int movieId) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<Movie> call = apiService.getMovieDetails(movieId, API_MOVIE_DB, CREDITS);

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();
                Log.d(TAG, "Movie data received: " + movie.toString());
                onFinishedListener.onFinished(movie);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.e(TAG, t.toString());
                onFinishedListener.onFailure(t);
            }
        });

    }
}
