package com.smovies.hk.searchmovies.movieDetail;

import com.smovies.hk.searchmovies.model.Movie;

public interface MovieDetailContract {
    interface Model {

        void getMovieDetails(OnFinishedListener onFinishedListener, int movieId);

        interface OnFinishedListener {
            void onFinished(Movie movie);

            void onFailure(Throwable t);
        }
    }

    interface View {

        void showProgress();

        void hideProgress();

        void setDataToViews(Movie movie);

        void onResponseFailure(Throwable throwable);
    }

    interface Presenter {
        void onDestroy();

        void requestMovieData(int movieId);
    }
}
