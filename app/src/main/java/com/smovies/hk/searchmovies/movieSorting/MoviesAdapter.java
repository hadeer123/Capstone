package com.smovies.hk.searchmovies.movieSorting;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.data.SaveMovieDBHandler;
import com.smovies.hk.searchmovies.data.SearchMovieContract;
import com.smovies.hk.searchmovies.model.Movie;
import com.smovies.hk.searchmovies.network.ApiClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ListMoviesViewHolder> {
    private static final String TAG = MoviesAdapter.class.getSimpleName();
    private MovieListFragment movieListFragment;
    private List<Movie> movieList;
    private Context mContext;


    public MoviesAdapter(MovieListFragment movieListFragment, List<Movie> movieList) {
        this.movieListFragment = movieListFragment;
        this.movieList = movieList;
    }

    public MoviesAdapter(List<Movie> movieList, Context context) {
        mContext = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public ListMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card_item, parent, false);

        return new ListMoviesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListMoviesViewHolder holder, final int position) {

        final Movie movie = movieList.get(position);
        final Context context;
        if (movieListFragment != null) {
            context = movieListFragment.getContext();
        } else {
            context = mContext;
        }


        holder.tvMovieTitle.setText(movie.getTitle());
        holder.tvMovieRatings.setText(String.valueOf(movie.getRating()));
        holder.tvReleaseDate.setText(movie.getReleaseDate());

        final SaveMovieDBHandler singleton = SaveMovieDBHandler.singleton();

        singleton.updateSaved(movie.getId(), holder.ivAddFav,
                context, SaveMovieDBHandler.FAV.SAVE.DEFAULT_VALUE_ID
                , SaveMovieDBHandler.FAV.UNSAVE.DEFAULT_VALUE_ID, SaveMovieDBHandler.FAV_URI);

        singleton.updateSaved(movie.getId(), holder.ivAddToWatch,
                context, SaveMovieDBHandler.TO_WATCH.SAVE.DEFAULT_VALUE_ID
                , SaveMovieDBHandler.TO_WATCH.UNSAVE.DEFAULT_VALUE_ID, SaveMovieDBHandler.TO_WATCH_URI);

        // loading album cover using Glide library
        Glide.with(context)
                .load(ApiClient.IMAGE_BASE_URL + movie.getThumbPath())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.pbLoadImage.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        holder.pbLoadImage.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(new RequestOptions().placeholder(R.drawable.ic_empty_movies).error(R.drawable.ic_empty_movies))
                .into(holder.ivMovieThumb);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movieListFragment.onMovieItemClick(position);
            }
        });

        holder.ivAddFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleton.ivDBOnClickHandler(movie, SearchMovieContract.searchMoviesEntry.CONTENT_URI_FAV
                        , holder.ivAddFav, null, null
                        , context);
            }
        });

        holder.ivAddToWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleton.ivDBOnClickHandler(movie, SearchMovieContract.searchMoviesEntry.CONTENT_URI_TO_WATCH
                        , holder.ivAddToWatch, null, null
                        , context);
            }
        });

    }


    @Override
    public int getItemCount() {
        return movieList.size();
    }


    public class ListMoviesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_movie_title) TextView tvMovieTitle;
        @BindView(R.id.tv_movie_ratings) TextView tvMovieRatings;
        @BindView(R.id.tv_release_date) TextView tvReleaseDate;
        @BindView(R.id.iv_movie_thumb) ImageView ivMovieThumb;
        @BindView(R.id.iv_add_to_watch) ImageView ivAddToWatch;
        @BindView(R.id.iv_add_fav) ImageView ivAddFav;
        @BindView(R.id.pb_load_image) ProgressBar pbLoadImage;

        public ListMoviesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
