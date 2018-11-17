package com.smovies.hk.searchmovies.movieSorting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smovies.hk.searchmovies.MovieDetailsActivity;
import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.model.Movie;
import com.smovies.hk.searchmovies.utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.smovies.hk.searchmovies.utils.Constants.KEY_MOVIE_ID;
import static com.smovies.hk.searchmovies.utils.GridSpacingItemDecoration.dpToPx;


public class MovieListFragment extends Fragment implements MovieListContract.View, MovieItemClickListener,
        ShowEmptyView {

    private static final String TAG = MovieListFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";

    int firstVisibleItem, visibleItemCount, totalItemCount;
    @BindView(R.id.rv_movie_list) RecyclerView rvMovieList;
    @BindView(R.id.pb_loading) ProgressBar pbLoading;
    @BindView(R.id.tv_empty_view) TextView tvEmptyView;

    private MovieListViewer movieListViewer;
    private List<Movie> moviesList;
    private MoviesAdapter moviesAdapter;
    private int pageNo = 1;

    //Constants for continuous Scroll
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    private GridLayoutManager mLayoutManager;
    private int tabNumber;
    private String dBQuery = null;

    public MovieListFragment() {
        // Required empty public constructor
    }

    public static MovieListFragment newInstance(int sectionNumber) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private void initUI() {
        moviesList = new ArrayList<>();
        moviesAdapter = new MoviesAdapter(this, moviesList);

        GridSetup();
    }

    private void GridSetup() {
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        rvMovieList.setLayoutManager(mLayoutManager);
        rvMovieList.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(Objects.requireNonNull(getContext()), 10), true));
        rvMovieList.setItemAnimator(new DefaultItemAnimator());
        rvMovieList.setAdapter(moviesAdapter);
    }


    private void setListeners() {

        rvMovieList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = rvMovieList.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                // Handling the infinite scroll
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }

                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    movieListViewer.getMoreData(pageNo, tabNumber);
                    loading = true;
                }

            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }


    @Override
    public void showProgress() {
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void setDataToRecyclerView(List<Movie> movieArrayList) {

        moviesList.addAll(movieArrayList);
        moviesAdapter.notifyDataSetChanged();

        //only do that if on the first three tabs
        if (tabNumber < 3) {
            pageNo++;
        }

    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
        Toast.makeText(getContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        movieListViewer.onDestroy();
    }

    @Override
    public void onMovieItemClick(int position) {

        if (position == -1) {
            return;
        }
        // openMovieDetails();
    }

    private void openMovieDetails() {
        Intent detailIntent = new Intent(getActivity(), MovieDetailsActivity.class);
        detailIntent.putExtra(KEY_MOVIE_ID, moviesList.get(tabNumber).getId());
        startActivity(detailIntent);
    }


    @Override
    public void showEmptyView() {
        rvMovieList.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyView() {
        rvMovieList.setVisibility(View.VISIBLE);
        tvEmptyView.setVisibility(View.GONE);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, rootView);
        initUI();
        setListeners();
        movieListViewer = new MovieListViewer(this);
        if (tabNumber < 3) {
            //retrofit pulling from internet
            movieListViewer.requestDataFromServer(tabNumber);
        } else {
            movieListViewer.requestDataFromDB(tabNumber, getContext());
        }
        return rootView;
    }

}
