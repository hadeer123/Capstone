package com.smovies.hk.searchmovies.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.movieDetail.MovieDetailsActivity;

import static com.smovies.hk.searchmovies.utils.Constants.KEY_MOVIE_ID;

/**
 * Implementation of App Widget functionality.
 */
public class ToWatchListAppWidget extends AppWidgetProvider {
    public static final String EXTRA_ITEM = "com.smovies.hk.searchmovies.widget.EXTRA_ITEM";
    public static final String START_MOVIE_ACTION = "com.smovies.hk.searchmovies.widget.START_MOVIE_ACTION";
    private static final String TAG = ToWatchListAppWidget.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
        for (int appWidgetId : appWidgetIds) {

            // Here we setup the intent which points to the WidgetUpdaterService which will
            // provide the views for this collection.
            Intent intent = new Intent(context, WidgetUpdaterService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.to_watch_list_app_widget);
            rv.setRemoteAdapter(R.id.widget_list, new Intent(context, WidgetUpdaterService.class));

            Intent broadcastIntent = new Intent(context, ToWatchListAppWidget.class);
            broadcastIntent.setAction(ToWatchListAppWidget.START_MOVIE_ACTION);
            broadcastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.widget_list, toastPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);

        if (intent.getAction().equals(START_MOVIE_ACTION)) {

            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            int movieIndex = intent.getIntExtra(KEY_MOVIE_ID, 0);

            Log.i(TAG, "Touched view " + viewIndex + "movie ID" + movieIndex + "app ID" + appWidgetId);


            startMovieDetailActivity(context, movieIndex);
        }

    }

    private void startMovieDetailActivity(Context context, int movieIndex) {
        Intent detailIntent = new Intent(context, MovieDetailsActivity.class);
        detailIntent.putExtra(KEY_MOVIE_ID, movieIndex);
        detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(detailIntent);
    }
}

