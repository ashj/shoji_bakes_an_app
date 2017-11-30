package com.example.shoji.bakingapp.widget;


import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.example.shoji.bakingapp.R;

import timber.log.Timber;

public class BakerAppWidgetService extends IntentService {
    public static final String ACTION_REFRESH_RECIPE_LIST = "com.example.shoji.bakingapp.action.refresh_recipe";
    public static final String ACTION_REFRESH_WIDGET = "com.example.shoji.bakingapp.action.refresh_widget";
    public static final String EXTRA_RECIPE_ID = "com.example.shoji.bakingapp.extra.recipe_id";

    public BakerAppWidgetService() {
        super(BakerAppWidgetService.class.getSimpleName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent == null)
            return;
        String action = intent.getAction();
        Timber.d("Got ACTION: %s", action);
        if (TextUtils.equals(action, ACTION_REFRESH_RECIPE_LIST)) {
            handleRefreshRecipe();
        } else if (TextUtils.equals(action, ACTION_REFRESH_WIDGET)) {
            handleRefreshWidget(intent);
        }
    }


    public static void startRefreshRecipe(Context context) {
        Timber.d("Started Action: %s", ACTION_REFRESH_RECIPE_LIST);
        Intent intent = new Intent(context, BakerAppWidgetService.class);
        intent.setAction(ACTION_REFRESH_RECIPE_LIST);
        context.startService(intent);
    }

    private void handleRefreshRecipe() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, WidgetRecipeList.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
    }

    public static void startRefreshWidget(Context context, Intent intent) {
        Timber.d("Started Action: %s", ACTION_REFRESH_RECIPE_LIST);
        intent.setAction(ACTION_REFRESH_WIDGET);
        context.startService(intent);
    }

    private void handleRefreshWidget(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        String recipeId = intent.getStringExtra(
                WidgetIngredientList.EXTRA_RECIPE_ID);

        String recipeName = intent.getStringExtra(
                WidgetIngredientList.EXTRA_RECIPE_NAME);

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_grid_view);

//        WidgetIngredientList.setIntent(intent);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_grid_view);


        Timber.d("handleRefreshWidget. got AppWidgetId:%d with recipeId: %s - %s", appWidgetId, recipeId, recipeName);
    }
}
