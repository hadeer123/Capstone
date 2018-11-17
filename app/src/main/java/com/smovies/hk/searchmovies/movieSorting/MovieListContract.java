package com.smovies.hk.searchmovies.movieSorting;


import android.content.Context;

import com.smovies.hk.searchmovies.model.Movie;

import java.util.List;

public interface MovieListContract {

    interface Model {

        void getMovieList(OnFinishedListener onFinishedListener, int pageNo, int tabNumber);

        void getMovieListFromDB(OnFinishedListener onFinishedListener, int tabNumber, Context mContext);

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

        void getMoreData(int pageNo, int tabNumber);

        void requestDataFromServer(int tabNumber);

        void requestDataFromDB(int tabNumber, Context mContext);

    }
}
