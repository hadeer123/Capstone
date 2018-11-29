package com.smovies.hk.searchmovies.movieDetail;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.model.Video;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.smovies.hk.searchmovies.utils.Constants.BASE;
import static com.smovies.hk.searchmovies.utils.Constants.IMG_0_JPG;
import static com.smovies.hk.searchmovies.utils.Constants.YOUTUBE_IMAGE_BASE_PATH;
import static com.smovies.hk.searchmovies.utils.Constants.YOUTUBE_VI;


public class VideoGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = VideoGridAdapter.class.getSimpleName();
    private TrailersFragment trailersFragment;
    private List<Video> mVideoList;

    public VideoGridAdapter(TrailersFragment trailersFragment, List<Video> mVideoList) {
        this.mVideoList = mVideoList;
        this.trailersFragment = trailersFragment;
    }

    public static Uri buildYouTubeThumbURI(String key) {
        return new Uri.Builder()
                .scheme(BASE)
                .appendEncodedPath(YOUTUBE_IMAGE_BASE_PATH)
                .appendEncodedPath(YOUTUBE_VI)
                .appendEncodedPath(key)
                .appendEncodedPath(IMG_0_JPG)
                .build();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Video video = mVideoList.get(position);
        Log.i(TAG, buildYouTubeThumbURI(video.getKey()).getPath());
        Glide.with(trailersFragment)
                .load(buildYouTubeThumbURI(video.getKey()))
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
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.ic_empty_movies).centerCrop().error(R.drawable.ic_empty_movies).centerCrop())
                .into(((ViewHolder) holder).ivVideoThumb);
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_view_video_thumb) ImageView ivVideoThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            trailersFragment.videoGridAdapterInteraction(v, getAdapterPosition());
        }

    }
}
