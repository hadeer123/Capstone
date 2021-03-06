package com.smovies.hk.searchmovies.movieSorting;

import android.content.Context;

import com.smovies.hk.searchmovies.model.Movie;

import java.util.List;

public class MovieListViewer implements MovieListContract.Presenter, MovieListContract.Model.OnFinishedListener {

    private MovieListContract.View movieListView;

    private MovieListContract.Model movieListModel;

    public MovieListViewer(MovieListContract.View movieListView) {
        this.movieListView = movieListView;
        movieListModel = new MovieListModel();
    }

    @Override
    public void onDestroy() {
        this.movieListView = null;
    }

    @Override
    public void getMoreData(int pageNo, String query, int tabNumber) {

        if (movieListView != null) {
            movieListView.showProgress();
        }
        movieListModel.getMovieList(this, query, pageNo, tabNumber);
    }

    @Override
    public void requestDataFromServer(int tabNumber, String query) {

        if (movieListView != null) {
            movieListView.showProgress();
        }
        movieListModel.getMovieList(this, query, 1, tabNumber);
    }

    @Override
    public void requestDataFromDB(int tabNumber, Context mContext, MovieListFragment fragment) {
        if (movieListView != null) {
            movieListView.showProgress();
        }
        movieListModel.getMovieListFromDB(this, tabNumber, mContext, fragment);
    }

    @Override
    public void onFinished(List<Movie> movieArrayList) {
        if (movieListView != null) {
            movieListView.hideProgress();
            movieListView.setDataToRecyclerView(movieArrayList);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        movieListView.onResponseFailure(t);
        if (movieListView != null) {
            movieListView.hideProgress();
        }
    }
}
