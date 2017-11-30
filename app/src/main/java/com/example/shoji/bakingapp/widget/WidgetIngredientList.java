package com.example.shoji.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.ui.RecipeActivity;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetIngredientList extends AppWidgetProvider {
    public static final String EXTRA_RECIPE_NAME = "extra_recipe_name";
    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = getGridRemoteView(context, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

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
        Timber.d("WidgetIngredientList - onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Timber.d("WidgetIngredientList - onDisabled");

    }


    private static RemoteViews getGridRemoteView(Context context, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_stack_view);

        // set intent for widget service that will create the views
        Intent serviceIntent = new Intent(context, StackWidgetService.class);

        remoteViews.setRemoteAdapter(R.id.stackWidgetView, serviceIntent);
        remoteViews.setEmptyView(R.id.stackWidgetView, R.id.stackWidgetEmptyView);

        // set intent for item click (opens main activity)
        Intent appIntent = new Intent(context, RecipeActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.stackWidgetView, appPendingIntent);

        return remoteViews;
    }

}

