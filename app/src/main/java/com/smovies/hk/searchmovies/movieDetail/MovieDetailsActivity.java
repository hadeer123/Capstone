package com.smovies.hk.searchmovies.movieDetail;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.data.SaveMovieDBHandler;
import com.smovies.hk.searchmovies.model.Cast;
import com.smovies.hk.searchmovies.model.Movie;
import com.smovies.hk.searchmovies.model.Video;
import com.smovies.hk.searchmovies.network.ApiClient;
import com.smovies.hk.searchmovies.utils.GridItemSpacing;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.smovies.hk.searchmovies.utils.Constants.BASE;
import static com.smovies.hk.searchmovies.utils.Constants.IMDB_BASE_PATH;
import static com.smovies.hk.searchmovies.utils.Constants.IMDB_TITLE_PATH;
import static com.smovies.hk.searchmovies.utils.Constants.KEY_MOVIE_ID;
import static com.smovies.hk.searchmovies.utils.Constants.MOVIE_PATH;
import static com.smovies.hk.searchmovies.utils.Constants.TMDB_BASE_PATH;
import static com.smovies.hk.searchmovies.utils.Constants.YOUTUBE_BASE_PATH;
import static com.smovies.hk.searchmovies.utils.Constants.YOUTUBE_WATCH_PATH;
import static com.smovies.hk.searchmovies.utils.GridItemSpacing.dpToPx;

public class MovieDetailsActivity extends AppCompatActivity implements MovieDetailContract.View, TrailersFragment.OnFragmentInteractionListener {
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    private static final SaveMovieDBHandler sH = SaveMovieDBHandler.singleton();
    @BindView(R.id.image_view_poster) ImageView ivPoster;
    @BindView(R.id.progress_bar_cast) ProgressBar pbLoadCast;
    @BindView(R.id.btn_fav) FloatingActionButton fbFavorite;
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
    @BindView(R.id.iv_backdrop) ImageView ivBackdrop;
    @BindView(R.id.movie_title_tv) TextView tvMovieTitle;
    @BindView(R.id.pb_load_backdrop) ProgressBar pbLoadBackdrop;
    @BindView(R.id.progress_bar) ProgressBar pbMainProgressBar;
    @BindView(R.id.main_content) View viewCoordinator;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.layout_movie_detail) ConstraintLayout clMovieDetail;
    private PublisherInterstitialAd mPublisherInterstitialAd;
    private CastAdapter castAdapter;
    private MovieDetailViewer movieDetailsPresenter;
    private TrailersFragment trailersFragment;
    private List<Cast> castList;
    private String imdbID;


    //@BindView(R.id.layout_movie_detail) ConstraintLayout mainContentLayout;

    private String movieName;
    private Movie movie;
    private Integer movieID;
    private MenuItem imdbMenuItem;

    public static void shareMovie(Activity activity, int movieID, String movieName) {

        URL shareURL = buildTMDBMovieURL(String.valueOf(movieID));
        String shareText = null;
        if (shareURL != null) {
            shareText = movieName + " - " + shareURL.toString();
        }

        if (shareText != null) {
            String mimeType = "text/plain";
            ShareCompat.IntentBuilder
                    .from(activity)
                    .setType(mimeType)
                    .setChooserTitle("Share: " + movieName)
                    .setText(shareText)
                    .startChooser();
        }
    }

    public static URL buildTMDBMovieURL(String movieId) {
        Uri uri = new Uri.Builder()
                .scheme(BASE)
                .appendEncodedPath(TMDB_BASE_PATH)
                .appendEncodedPath(MOVIE_PATH)
                .appendEncodedPath(movieId)
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void openIMDB(Context context, String imdbId) {
        if (imdbId != null) {
            URL url = buildIMDBURL(imdbId);
            if (url != null) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                context.startActivity(webIntent);
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.imdb_link_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    public static URL buildIMDBURL(String imdbId) {
        Uri uri = new Uri.Builder()
                .scheme(BASE)
                .appendEncodedPath(IMDB_BASE_PATH)
                .appendEncodedPath(IMDB_TITLE_PATH)
                .appendEncodedPath(imdbId)
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    public static URL buildYouTubeURL(String key) {
        Uri uri = new Uri.Builder()
                .scheme(BASE)
                .appendEncodedPath(YOUTUBE_BASE_PATH)
                .appendEncodedPath(YOUTUBE_WATCH_PATH)
                .appendQueryParameter("v", key)
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initCollapsingToolbar();
        initUI();
        initAds();
    }

    private void initAds() {
        mPublisherInterstitialAd = new PublisherInterstitialAd(this);
        mPublisherInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));

        mPublisherInterstitialAd.loadAd(new PublisherAdRequest.Builder().build());

        mPublisherInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Toast.makeText(getApplicationContext(), "Ad did not load", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mPublisherInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });
    }

    private void initUI() {
        castList = new ArrayList<>();
        castAdapter = new CastAdapter(this, castList);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, true);
        rvCast.setLayoutManager(mLayoutManager);
        rvCast.addItemDecoration(new GridItemSpacing(1, dpToPx(Objects.requireNonNull(this), 5), true));
        rvCast.setItemAnimator(new DefaultItemAnimator());
        rvCast.setAdapter(castAdapter);

        movieDetailsPresenter = new MovieDetailViewer(this);
        movieID = getIntent().getIntExtra(KEY_MOVIE_ID, 0);
        movieDetailsPresenter.requestMovieData(movieID);
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
        pbMainProgressBar.setVisibility(View.VISIBLE);
        clMovieDetail.setVisibility(View.GONE);
        pbLoadBackdrop.setVisibility(View.VISIBLE);
        pbLoadCast.setVisibility(View.VISIBLE);

        if (trailersFragment != null)
            trailersFragment.showProgress();
    }

    @Override
    public void hideProgress() {
        clMovieDetail.setVisibility(View.VISIBLE);

        pbMainProgressBar.setVisibility(View.GONE);
        pbLoadBackdrop.setVisibility(View.GONE);
        pbLoadCast.setVisibility(View.GONE);

        if (trailersFragment != null)
            trailersFragment.hideProgress();
    }

    private void updateFab(final Movie movie) {
        sH.updateSaved(movie.getId(), fbFavorite, getApplicationContext(), SaveMovieDBHandler.FAV.SAVE_LIGHT.DEFAULT_VALUE_ID
                , SaveMovieDBHandler.FAV.UNSAVE_LIGHT.DEFAULT_VALUE_ID, SaveMovieDBHandler.FAV_URI);
        fbFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sH.ivDBOnClickHandler(movie, SaveMovieDBHandler.FAV_URI, null, fbFavorite, null, getBaseContext());
            }
        });
    }

    public void updateCastView(Movie movie) {
        castList.clear();
        castList.addAll(movie.getCredits().getCast());
        castAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Snackbar.make(viewCoordinator, getString(R.string.error_data), Snackbar.LENGTH_LONG).show();
        if (clMovieDetail.getVisibility() == View.GONE) {
            tvErrorMsg.setVisibility(View.VISIBLE);
        }
        if (trailersFragment != null)
            trailersFragment.onResponseFailure(throwable);
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

    @Override
    public void setDataToViews(final Movie movie) {
        if (movie != null) {
            this.movie = movie;
            updateFab(movie);
            movieName = movie.getTitle();
            tvMovieTitle.setText(movieName);
            tvMovieTitleSub.setText(movie.getTitle());
            tvMovieReleaseDate.setText(movie.getReleaseDate());
            tvMovieRatings.setText(String.valueOf(movie.getRating()));
            tvRuntimeValue.setText(String.format("%s%s", movie.getRunTime(), getString(R.string.minutes)));
            tvMovieDescription.setText(movie.getOverview());
            ratingBar.setRating(movie.getRating());

            imdbID = movie.getImdbId();
            imdbMenuItem.setVisible(true);

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

            updateCastView(movie);

            if (trailersFragment != null)
                trailersFragment.setDataToViews(movie);
        }

    }

    private void updateToWatchTable(MenuItem item) {
        if (movie != null)
            sH.ivDBOnClickHandler(movie, SaveMovieDBHandler.TO_WATCH_URI, null, null, item, getApplicationContext());
    }


    @Override
    public void sendFragmentInstance(TrailersFragment fragment) {
        this.trailersFragment = fragment;
    }

    @Override
    public void onVideoAdapterItemSelected(View v, Video video) {
        openYoutube(getApplicationContext(), video.getKey());
    }

    public void openYoutube(Context context, String key) {
        if (key != null) {
            try {
                Intent youtubeAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd:youtube:" + key));
                context.startActivity(youtubeAppIntent);
            } catch (ActivityNotFoundException e) {
                URL url = buildYouTubeURL(key);
                if (url != null) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                    context.startActivity(webIntent);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        imdbMenuItem = menu.findItem(R.id.action_imdb);
        menu.findItem(R.id.action_toWatch).setIcon(sH.updateSavedResource(movieID, getApplicationContext(), SaveMovieDBHandler.TO_WATCH.SAVE_LIGHT.DEFAULT_VALUE_ID,
                SaveMovieDBHandler.TO_WATCH.UNSAVE_LIGHT.DEFAULT_VALUE_ID, SaveMovieDBHandler.TO_WATCH_URI));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toWatch:
                updateToWatchTable(item);
                return true;
            case R.id.action_imdb:
                if (imdbID.isEmpty()) {
                    item.setVisible(false);
                } else {
                    openIMDB(getApplicationContext(), imdbID);
                }
                return true;
            case R.id.action_share:
                shareMovie(this, movieID, movieName);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
