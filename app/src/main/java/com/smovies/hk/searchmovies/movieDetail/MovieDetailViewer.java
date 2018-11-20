package com.smovies.hk.searchmovies.movieDetail;

import com.smovies.hk.searchmovies.model.Movie;

public class MovieDetailViewer implements MovieDetailContract.Presenter, MovieDetailContract.Model.OnFinishedListener {
    private MovieDetailContract.View movieDetailView;
    private MovieDetailContract.Model movieDetailsModel;

    public MovieDetailViewer(MovieDetailContract.View movieDetailView) {
        this.movieDetailView = movieDetailView;
        this.movieDetailsModel = new MovieDetailsModel();
    }

    @Override
    public void onDestroy() {
        movieDetailView = null;
    }

    @Override
    public void requestMovieData(int movieId) {

        if (movieDetailView != null) {
            movieDetailView.showProgress();
        }
        movieDetailsModel.getMovieDetails(this, movieId);
    }


    @Override
    public void onFinished(Movie movie) {

        if (movieDetailView != null) {
            movieDetailView.hideProgress();
        }
        movieDetailView.setDataToViews(movie);
    }

    @Override
    public void onFailure(Throwable t) {
        if (movieDetailView != null) {
            movieDetailView.hideProgress();
        }
        movieDetailView.onResponseFailure(t);
    }
}
