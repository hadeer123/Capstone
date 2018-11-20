package com.smovies.hk.searchmovies.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;
    public static final String API_MOVIE_DB = "8934d5a9f489ef4ac9de9c6fb5aa6b7a";
    public static final String YOUTUBE_API_KEY = "AIzaSyALK7WMYE1MWRJKfxWDlgRm6rWbuAKJqGs";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w342/";
    public static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/";
    public static final String BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780/";
    public static final String IMAGE_SIZE_SMALL = "w342";
    public static final String IMAGE_SIZE_MEDIUM = "w500";
    public static final String IMAGE_SIZE_XLARGE = "w780";
    /**
     * This method returns retrofit client instance
     *
     * @return Retrofit object
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
