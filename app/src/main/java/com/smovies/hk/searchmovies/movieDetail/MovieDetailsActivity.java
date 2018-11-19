package com.smovies.hk.searchmovies.movieDetail;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import static com.smovies.hk.searchmovies.utils.Constants.KEY_MOVIE_ID;


public class MovieDetailsActivity extends AppCompatActivity implements MovieDetailContract.View {

    @BindView(R.id.iv_backdrop) ImageView ivBackdrop;
    @BindView(R.id.image_view_poster) ImageView ivPoster;
    @BindView(R.id.pb_load_backdrop) ProgressBar pbLoadBackdrop;
    @BindView(R.id.tv_movie_title) TextView tvMovieTitle;
    @BindView(R.id.movie_description) TextView tvMovieDescription;
    @BindView(R.id.text_view_movie_title) TextView tvMovieTitleSub;
    @BindView(R.id.text_view_movie_releaseDate) TextView tvMovieReleaseDate;
    @BindView(R.id.text_view_rating) TextView tvMovieRatings;
    @BindView(R.id.text_view_movie_runtime) TextView tvRuntimeValue;
    @BindView(R.id.pb_cast_loading) ProgressBar pbLoadCast;
    @BindView(R.id.main_content) View viewCoordinator;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;

    // @BindView(R.id.tv_movie_overview) TextView tvOverview;
    private CastAdapter castAdapter;
    private List<Cast> castList;
//  private @BindView(R.id.tv_homepage_value) TextView tvHomepageValue;
//  private @BindView(R.id.tv_tagline_value) TextView tvTaglineValue;

//  @BindView(R.id.btn_fav) FloatingActionButton favBtn;
//  @BindView(R.id.btn_toWatchList) FloatingActionButton toWatchBtn;


    private String movieName;

    private MovieDetailViewer movieDetailsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initCollapsingToolbar();

        initUI();

        Intent mIntent = getIntent();
        int movieId = mIntent.getIntExtra(KEY_MOVIE_ID, 0);

        movieDetailsPresenter = new MovieDetailViewer(this);
        movieDetailsPresenter.requestMovieData(movieId);

    }

    /**
     * Initializing UI components
     */
    private void initUI() {
        castList = new ArrayList<>();
        RecyclerView rvCast = findViewById(R.id.rv_cast);
        castAdapter = new CastAdapter(this, castList);
        rvCast.setAdapter(castAdapter);
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {

        collapsingToolbar.setTitle(" ");
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(movieName);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void showProgress() {
        pbLoadBackdrop.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pbLoadCast.setVisibility(View.GONE);
    }

    @Override
    public void setDataToViews(Movie movie) {

        if (movie != null) {

            movieName = movie.getTitle();

            tvMovieTitle.setText(movie.getTitle());
            tvMovieTitleSub.setText(movie.getTitle());
            tvMovieReleaseDate.setText(movie.getReleaseDate());
            tvMovieRatings.setText(String.valueOf(movie.getRating()));
            tvRuntimeValue.setText(movie.getRunTime());
            tvMovieDescription.setText(movie.getOverview());

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

            // loading album cover using Glide library
            Glide.with(this)
                    .load(ApiClient.BACKDROP_BASE_URL + movie.getBackdropPath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            pbLoadBackdrop.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            pbLoadBackdrop.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .apply(new RequestOptions().placeholder(R.drawable.ic_empty_movies).error(R.drawable.ic_empty_movies))
                    .into(ivBackdrop);

            castList.clear();
            castList.addAll(movie.getCredits().getCast());
            castAdapter.notifyDataSetChanged();

//            tvTaglineValue.setText(movie.getTagline() != null ? movie.getTagline() : "N/A");
//            tvHomepageValue.setText(movie.getHomepage() != null ? movie.getHomepage() : "N/A");
//            tvRuntimeValue.setText(movie.getRunTime() != null ? movie.getRunTime() : "N/A");
        }

    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Snackbar.make(viewCoordinator, getString(R.string.error_data), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        movieDetailsPresenter.onDestroy();
    }

}
