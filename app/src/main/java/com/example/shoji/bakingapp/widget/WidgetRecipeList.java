package com.example.shoji.bakingapp.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.ui.MainActivity;
import com.example.shoji.bakingapp.ui.RecipeActivity;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetRecipeList extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        RemoteViews views = getGridRemoteView(context);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static RemoteViews getGridRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);
        Intent intent = new Intent(context, RecipeGridWidgetService.class);

        Intent appIntent = new Intent(context, RecipeActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);

        views.setRemoteAdapter(R.id.widget_grid_view, intent);
        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);
        return views;
    }

}

