package com.smovies.hk.searchmovies.movieSorting;


import com.smovies.hk.searchmovies.model.Movie;

import java.util.List;

public interface MovieListContract {

    interface Model {

        void getMovieList(OnFinishedListener onFinishedListener, int pageNo, int tabNumber);

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

    }
}
