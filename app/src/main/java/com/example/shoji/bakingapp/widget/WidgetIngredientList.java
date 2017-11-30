package com.example.shoji.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
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
                                int appWidgetId, String recipeId, String recipeName) {

        RemoteViews views = getGridRemoteView(context, recipeId, recipeName);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        String recipeId = "2";
        String recipeName = "Argh";

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeId, recipeName);
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


    private static RemoteViews getGridRemoteView(Context context, String recipeId, String recipeName) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);
        Intent intent = new Intent(context, IngredientGridWidgetService.class);

        intent.putExtra(WidgetIngredientList.EXTRA_RECIPE_ID, recipeId);

        intent.putExtra(WidgetIngredientList.EXTRA_RECIPE_NAME, recipeName);

        //Intent appIntent = new Intent(context, RecipeActivity.class);
        //PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);

        views.setRemoteAdapter(R.id.widget_grid_view, intent);
        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);
        return views;
    }

}

