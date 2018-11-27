package com.smovies.hk.searchmovies.network;

import com.smovies.hk.searchmovies.model.Movie;
import com.smovies.hk.searchmovies.model.MovieListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("movie/popular")
    Call<MovieListResponse> getPopularMoviesList(@Query("api_key") String apiKey, @Query("page") int PageNo);

    @GET("movie/top_rated")
    Call<MovieListResponse> getTopRatedMoviesList(@Query("api_key") String apiKey, @Query("page") int PageNo);

    @GET("movie/now_playing")
    Call<MovieListResponse> getNowPlayingMoviesList(@Query("api_key") String apiKey, @Query("page") int PageNo);

    @GET("search/movie")
    Call<MovieListResponse> getMoviesList(@Query("api_key") String apiKey, @Query("query") String query, @Query("page") int PageNo);

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(@Path("movie_id") int movieId, @Query("api_key") String apiKey, @Query("append_to_response") String creditsAndVideos);
}
