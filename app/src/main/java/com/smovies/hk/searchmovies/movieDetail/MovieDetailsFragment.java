package com.smovies.hk.searchmovies.movieDetail;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.model.Cast;
import com.smovies.hk.searchmovies.model.Movie;
import com.smovies.hk.searchmovies.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment implements MovieDetailContract.View {

    private static String ARGS_MOVIE_ID = "movie_id";
    @BindView(R.id.image_view_poster) ImageView ivPoster;
    @BindView(R.id.progress_bar_cast) ProgressBar pbLoadCast;
    @BindView(R.id.text_view_movie_plot) TextView tvMovieDescription;
    @BindView(R.id.text_view_movie_title) TextView tvMovieTitleSub;
    @BindView(R.id.text_view_movie_release_date) TextView tvMovieReleaseDate;
    @BindView(R.id.text_view_rating) TextView tvMovieRatings;
    @BindView(R.id.text_view_movie_runtime) TextView tvRuntimeValue;
    @BindView(R.id.text_view_rating_out_of) TextView tvRatingOutOf;
    @BindView(R.id.text_view_rating_num) TextView tvRatingNum;
    @BindView(R.id.rating_bar) RatingBar ratingBar;
    @BindView(R.id.recycler_view_cast) RecyclerView rvCast;
    @BindView(R.id.text_view_error_message) TextView tvErrorMsg;
    private CastAdapter castAdapter;
    private List<Cast> castList;
    private OnFragmentInteractionListener mListener;
    private MovieDetailViewer movieDetailsPresenter;
    private int movieID;


    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    public static MovieDetailsFragment newInstance(int movieID) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_MOVIE_ID, movieID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieID = getArguments().getInt(ARGS_MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this, view);

        initUI();
        // Inflate the layout for this fragment
        return view;
    }

    private void initUI() {
        castList = new ArrayList<>();
        //castAdapter = new CastAdapter(this, castList);
        rvCast.setAdapter(castAdapter);
        mListener.onFragmentCreated();

        movieDetailsPresenter = new MovieDetailViewer(this);
        movieDetailsPresenter.requestMovieData(movieID);
        mListener.updateMovieDetailViewer(movieDetailsPresenter);
    }

    @Override
    public void showProgress() {
        pbLoadCast.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pbLoadCast.setVisibility(View.GONE);
    }


    @Override
    public void setDataToViews(Movie movie) {
        if (movie != null) {
            tvMovieTitleSub.setText(movie.getTitle());
            tvMovieReleaseDate.setText(movie.getReleaseDate());
            tvMovieRatings.setText(String.valueOf(movie.getRating()));
            tvRuntimeValue.setText(movie.getRunTime() + getString(R.string.minutes));
            tvMovieDescription.setText(movie.getOverview());
            ratingBar.setRating(movie.getRating());
            // tvRatingNum.setText(movie.);

            Glide.with(this)
                    .load(ApiClient.POSTER_BASE_URL + movie.getThumbPath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .apply(new RequestOptions().placeholder(R.drawable.ic_empty_movies).error(R.drawable.ic_empty_movies))
                    .into(ivPoster);

            castList.clear();
            castList.addAll(movie.getCredits().getCast());
            castAdapter.notifyDataSetChanged();
            boolean yes = castAdapter.hasObservers();
        }

    }

    public void onResponseFailure(Throwable throwable) {
        tvErrorMsg.setVisibility(View.VISIBLE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        movieDetailsPresenter.onDestroy();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentCreated();

        void updateMovieDetailViewer(MovieDetailViewer movieDetailViewer);
    }
}
