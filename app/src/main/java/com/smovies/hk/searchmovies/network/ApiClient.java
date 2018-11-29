package com.smovies.hk.searchmovies.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w342/";
    public static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/";
    public static final String BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780/";
    public static final String IMAGE_SIZE_SMALL = "w342";
    public static final String IMAGE_SIZE_MEDIUM = "w500";
    public static final String IMAGE_SIZE_XLARGE = "w780";
    private static Retrofit retrofit = null;

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
