package com.smovies.hk.searchmovies.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.Target;
import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.data.SearchMovieContract;
import com.smovies.hk.searchmovies.network.ApiClient;

import java.util.concurrent.ExecutionException;

import static com.smovies.hk.searchmovies.utils.Constants.KEY_MOVIE_ID;

public class WidgetUpdaterService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new WidgetRemoteFactory(this.getApplicationContext());
    }

    private class WidgetRemoteFactory implements RemoteViewsFactory {

        Context mContext;
        Cursor mCursor;

        WidgetRemoteFactory(Context applicationContext) {
            mContext = applicationContext;

        }

        @Override
        public void onCreate() {
            initCursor();
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
        }

        @Override
        public void onDataSetChanged() {
            initCursor();
        }

        private void initCursor() {
            Uri TO_WATCH_URI = SearchMovieContract.searchMoviesEntry.CONTENT_URI_TO_WATCH;
            if (mCursor != null) mCursor.close();
            mCursor = mContext.getContentResolver().query(
                    TO_WATCH_URI,
                    null,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onDestroy() {
            mCursor.close();
        }

        @Override
        public int getCount() {
            if (mCursor == null) return 0;
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getLoadingView() {
            // You can create a custom loading view (for instance when getViewAt() is slow.) If you
            // return null here, you will get the default loading view.
            return null;
        }


        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

            if (mCursor == null || mCursor.getCount() == 0) {
                views.setViewVisibility(R.id.empty_view, View.VISIBLE);
            }

            mCursor.moveToPosition(position);
            views.setViewVisibility(R.id.empty_view, View.GONE);
            int idC = mCursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_ID);
            int titleC = mCursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_TITLE);
            int releaseDateC = mCursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RELEASE_DATE);
            int ratingC = mCursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_RATING);
            int thumbPathC = mCursor.getColumnIndex(SearchMovieContract.searchMoviesEntry.COLUMN_MOVIE_THUMB_PATH);


            int thisId = mCursor.getInt(idC);
            String thisTitle = mCursor.getString(titleC);

            String thisReleaseDate = mCursor.getString(releaseDateC);
            float thisRatings = mCursor.getFloat(ratingC);
            final String thisThumbPath = mCursor.getString(thumbPathC);


            views.setTextViewText(R.id.movie_title_tv_w, thisTitle);
            views.setTextViewText(R.id.tv_release_date, thisReleaseDate);
//         views.setTextViewText(R.id.tv_movie_ratings_w, String.valueOf(thisRatings));

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
            ComponentName thisAppWidget = new ComponentName(mContext.getPackageName(), ToWatchListAppWidget.class.getName());
            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(mContext, R.id.iv_movie_thumb, views, thisAppWidget);


            Bitmap myBitmap = null;
            try {
                myBitmap = Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(ApiClient.IMAGE_BASE_URL + thisThumbPath)
                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            views.setImageViewBitmap(R.id.iv_movie_thumb, myBitmap);

            Bundle extras = new Bundle();
            extras.putInt(ToWatchListAppWidget.EXTRA_ITEM, position);
            extras.putInt(KEY_MOVIE_ID, thisId);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.linearlayout_movie_towatch, fillInIntent);

            return views;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


    }
}
