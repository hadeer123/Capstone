package com.smovies.hk.searchmovies.movieSorting;


import android.content.Context;

import com.smovies.hk.searchmovies.model.Movie;

import java.util.List;

public interface MovieListContract {

    interface Model {

        void getMovieList(OnFinishedListener onFinishedListener, String query, int pageNo, int tabNumber);

        void getMovieListFromDB(OnFinishedListener onFinishedListener, int tabNumber, Context mContext, MovieListFragment movieListFragment);

        interface OnFinishedListener {
            void onFinished(List<Movie> movieArrayList);

            void onFailure(Throwable t);
        }

    }

    interface View {

        void showProgress();

        void hideProgress();

        void setDataToRecyclerView(List<Movie> movieArrayList);

        void onResponseFailure(Throwable throwable);

    }

    interface Presenter {

        void onDestroy();

        void getMoreData(int pageNo, String query, int tabNumber);

        void requestDataFromServer(int tabNumber, String query);

        void requestDataFromDB(int tabNumber, Context mContext, MovieListFragment fragment);

    }
}
