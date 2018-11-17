package com.smovies.hk.searchmovies.movieSorting;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.smovies.hk.searchmovies.R;
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


    public MoviesAdapter(MovieListFragment movieListFragment, List<Movie> movieList) {
        this.movieListFragment = movieListFragment;
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

        holder.tvMovieTitle.setText(movie.getTitle());
        holder.tvMovieRatings.setText(String.valueOf(movie.getRating()));
        holder.tvReleaseDate.setText(movie.getReleaseDate());
        updateSavedToFav(movie, holder.ivAddFav);
        updateSavedToWatch(movie, holder.ivAddToWatch);

        // loading album cover using Glide library
        Glide.with(movieListFragment)
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
                if (!isSaved(movie, SearchMovieContract.searchMoviesEntry.CONTENT_URI_FAV))
                    saveToDB(holder.ivAddFav, movie, SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_FAV);
                else
                    unSave(holder.ivAddFav, movie, SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_FAV);
            }
        });

        holder.ivAddToWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSaved(movie, SearchMovieContract.searchMoviesEntry.CONTENT_URI_TO_WATCH))
                    saveToDB(holder.ivAddFav, movie, SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_WATCH);
                else
                    unSave(holder.ivAddFav, movie, SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_FAV);
            }
        });

    }

    private void updateSavedToFav(Movie movie, ImageView imageView) {
        int imgResFav = isSaved(movie, SearchMovieContract.searchMoviesEntry.CONTENT_URI_FAV) ?
                R.mipmap.ic_fav_full : R.mipmap.ic_fav_empty;
        imageView.setImageResource(imgResFav);
    }

    private void updateSavedToWatch(Movie movie, ImageView imageView) {
        int imgResToWatch = isSaved(movie, SearchMovieContract.searchMoviesEntry.CONTENT_URI_TO_WATCH) ?
                R.mipmap.ic_plus_full : R.mipmap.ic_plus_empty;
        imageView.setImageResource(imgResToWatch);
    }

    public boolean isSaved(Movie movie, Uri uri) {
        int id = movie.getId();
        String stringId = Integer.toString(id);
        uri = uri.buildUpon().appendPath(stringId).build();
        Cursor cursor = movieListFragment.getActivity()
                .getContentResolver().query(uri, null, null, null, null, null);
        return cursor.getCount() != 0;
    }

    private void unSave(ImageView imageView, Movie movie, String saveIn) {
        //TODO delete instead of update
        contentProviderDeleteEntry(movie, SearchMovieContract.searchMoviesEntry.CONTENT_URI);
        updateViewAfterAction(imageView, movie, saveIn);
    }

    public void contentProviderDeleteEntry(Movie movie, Uri uri) {
        int id = movie.getId();
        String stringId = Integer.toString(id);
        uri = uri.buildUpon().appendPath(stringId).build();
        movieListFragment.getActivity().getContentResolver().delete(uri, null, null);
    }

    private void updateViewAfterAction(ImageView imageView, Movie movie, String saveIn) {
        if (saveIn.equals(SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_FAV))
            updateSavedToFav(movie, imageView);
        else
            updateSavedToWatch(movie, imageView);
    }

    public void saveToDB(ImageView imageView, Movie movie, String DBColumn) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_TITLE, movie.getTitle());

        contentValues.put(DBColumn, 1);
        Uri path = SearchMovieContract.searchMoviesEntry.CONTENT_URI;

        Uri uri = movieListFragment.getActivity()
                .getContentResolver().insert(path, contentValues);

        if (uri != null) {
            Toast.makeText(movieListFragment.getContext(), uri.toString(), Toast.LENGTH_SHORT).show();
            if (DBColumn.equals(SearchMovieContract.searchMoviesEntry.COLUMN_SAVE_TO_FAV)) {
                updateSavedToFav(movie, imageView);
            } else {
                updateSavedToWatch(movie, imageView);
            }

        }
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
