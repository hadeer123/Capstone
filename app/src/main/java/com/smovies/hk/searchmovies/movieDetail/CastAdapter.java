package com.smovies.hk.searchmovies.movieDetail;

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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.model.Cast;
import com.smovies.hk.searchmovies.network.ApiClient;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.MyViewHolder> {

    private Context mContext;
    private List<Cast> castList;

    public CastAdapter(Context mContext, List<Cast> castList) {
        this.mContext = mContext;
        this.castList = castList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_film_cast, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        Cast cast = castList.get(position);

        holder.tvCharacter.setText(cast.getCharacter());
        holder.tvName.setText(cast.getName());

        // loading cast profile pic using Glide library
        Glide.with(mContext)
                .load(ApiClient.IMAGE_BASE_URL + cast.getProfilePath())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.pbLoadProfile.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.pbLoadProfile.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(new RequestOptions().placeholder(R.drawable.ic_empty_movies).error(R.drawable.ic_empty_movies))
                .into(holder.ivCastPhoto);
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_character_name) TextView tvCharacter;
        @BindView(R.id.text_view_cast_name) TextView tvName;
        @BindView(R.id.image_view_cast_photo) ImageView ivCastPhoto;
        @BindView(R.id.pb_load_profile) ProgressBar pbLoadProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
