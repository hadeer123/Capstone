package com.smovies.hk.searchmovies.movieSorting;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.model.Movie;
import com.smovies.hk.searchmovies.movieDetail.MovieDetailsActivity;
import com.smovies.hk.searchmovies.network.NetworkUtils;
import com.smovies.hk.searchmovies.utils.GridItemSpacing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.smovies.hk.searchmovies.utils.Constants.FAV_LIST;
import static com.smovies.hk.searchmovies.utils.Constants.GET_MOVIES;
import static com.smovies.hk.searchmovies.utils.Constants.KEY_MOVIE_ID;
import static com.smovies.hk.searchmovies.utils.Constants.PLAYING_NOW;
import static com.smovies.hk.searchmovies.utils.Constants.POPULAR;
import static com.smovies.hk.searchmovies.utils.Constants.TOP_RATED;
import static com.smovies.hk.searchmovies.utils.Constants.TO_WATCH_LIST;
import static com.smovies.hk.searchmovies.utils.GridItemSpacing.dpToPx;

public class MovieListFragment extends Fragment implements MovieListContract.View, MovieItemClickListener,
        ShowEmptyView {
    private static final String KEY_INSTANCE_STATE_RV_POSITION = "rv_position";
    private static final String KEY_INSTANCE_STATE_SCROLL_X = "rv_scroll_x";
    private static final String KEY_INSTANCE_STATE_SCROLL_Y = "rv_scroll_y";
    private MovieListFragmentListener movieListFragmentListener;
    private int scrollX, scrollY;
    private Parcelable mLayoutManagerSavedState;

    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARGS_SEARCH_QUERY = "search_query";
    private static final String TAG = MovieListFragment.class.getSimpleName();
    private static final int SPAN_COUNT_PORTRAIT = 2;
    private static final int SPAN_COUNT_LANDSCAPE = 4;
    private static final int PADDING_DP = 10;

    int firstVisibleItem, visibleItemCount, totalItemCount;
    @BindView(R.id.rv_movie_list) RecyclerView rvMovieList;
    @BindView(R.id.pb_loading) ProgressBar pbLoading;
    @BindView(R.id.tv_empty_view) TextView tvEmptyView;
    @BindView(R.id.tv_no_inter_view_view) TextView tvNoInternet;
    @BindView(R.id.error_view) RelativeLayout rlErrorView;

    private MovieListViewer movieListViewer;
    private List<Movie> moviesList;
    private MoviesAdapter moviesAdapter;
    private int pageNo = 1;

    //Constants for continuous Scroll
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    private GridLayoutManager mLayoutManager;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_INSTANCE_STATE_RV_POSITION, rvMovieList.getLayoutManager().onSaveInstanceState());
        outState.putInt(KEY_INSTANCE_STATE_SCROLL_X, rvMovieList.getScrollX());
        outState.putInt(KEY_INSTANCE_STATE_SCROLL_Y, rvMovieList.getScrollY());
        movieListFragmentListener.updateCurrentPage(tabNumber);
    }
    private int tabNumber;
    private String queryString;

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

    public static MovieListFragment newInstance(int sectionNumber, String queryString) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARGS_SEARCH_QUERY, queryString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MovieListFragment.MovieListFragmentListener) {
            movieListFragmentListener = (MovieListFragment.MovieListFragmentListener) context;
        }
        switch (tabNumber) {
            case FAV_LIST:
                Objects.requireNonNull(getActivity()).setTitle(getString(R.string.nav_favorites_t));
                break;
            case TO_WATCH_LIST:
                Objects.requireNonNull(getActivity()).setTitle(getString(R.string.nav_to_watch_t));
                break;
            default:
                break;
        }
    }

    private void initUI() {
        moviesList = new ArrayList<>();
        moviesAdapter = new MoviesAdapter(this, moviesList, tabNumber);
        int currentOrientation = getResources().getConfiguration().orientation;
        int spanCount = SPAN_COUNT_PORTRAIT;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        float density = displayMetrics.xdpi;

        if (density <= 600 && density >= 480) {
            spanCount = SPAN_COUNT_PORTRAIT + 1;
        } else if (density > 600) {
            spanCount = SPAN_COUNT_LANDSCAPE;
        }

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape
            spanCount = SPAN_COUNT_LANDSCAPE;
        }
        GridSetup(spanCount);
    }

    private void GridSetup(int spanCount) {
        mLayoutManager = new GridLayoutManager(getContext(), spanCount);
        rvMovieList.setLayoutManager(mLayoutManager);
        rvMovieList.addItemDecoration(new GridItemSpacing(spanCount, dpToPx(Objects.requireNonNull(getContext()), PADDING_DP), true));
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
                    if (tabNumber < 3)
                        movieListViewer.getMoreData(pageNo, null, tabNumber);
                    loading = true;
                }

            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLayoutManagerSavedState = savedInstanceState.getParcelable(KEY_INSTANCE_STATE_RV_POSITION);
            scrollX = savedInstanceState.getInt(KEY_INSTANCE_STATE_SCROLL_X);
            scrollY = savedInstanceState.getInt(KEY_INSTANCE_STATE_SCROLL_Y);
        }

        if (getArguments() != null) {
            tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            queryString = getArguments().getString(ARGS_SEARCH_QUERY);
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

        if (mLayoutManagerSavedState != null) {
            rvMovieList.getLayoutManager().onRestoreInstanceState(mLayoutManagerSavedState);
            rvMovieList.scrollTo(scrollX, scrollY);
        }

    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
        String msg;
        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            msg = getString(R.string.no_internet);
        } else {
            msg = getString(R.string.connection_error);
        }

        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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
        openMovieDetails(position);
    }

    private void openMovieDetails(int position) {
        Intent detailIntent = new Intent(getActivity(), MovieDetailsActivity.class);
        int movieID = moviesList.get(position).getId();
        detailIntent.putExtra(KEY_MOVIE_ID, movieID);
        startActivity(detailIntent);
    }


    @Override
    public void showEmptyView() {
        rlErrorView.setVisibility(View.VISIBLE);
        rvMovieList.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyView() {
        rlErrorView.setVisibility(View.GONE);
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
        getActivity().setTitle(R.string.app_name);
        if (NetworkUtils.isNetworkAvailable(getContext())) {
            rlErrorView.setVisibility(View.GONE);
            tvNoInternet.setVisibility(View.GONE);
            retrieveMovies();
        } else {
            rlErrorView.setVisibility(View.VISIBLE);
            tvNoInternet.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    private void retrieveMovies() {
        switch (tabNumber) {
            case PLAYING_NOW:
            case POPULAR:
            case TOP_RATED:
                movieListViewer.requestDataFromServer(tabNumber, null);
                break;
            case GET_MOVIES:
                movieListViewer.requestDataFromServer(tabNumber, queryString);
                break;
            case FAV_LIST:
                getActivity().setTitle(R.string.nav_favorites_t);
                movieListViewer.requestDataFromDB(tabNumber, getContext(), this);
                break;
            case TO_WATCH_LIST:
                getActivity().setTitle(R.string.nav_to_watch_t);
                movieListViewer.requestDataFromDB(tabNumber, getContext(), this);
                break;
        }
    }

    public interface MovieListFragmentListener {
        void updateCurrentPage(int tabNumber);
    }

}
