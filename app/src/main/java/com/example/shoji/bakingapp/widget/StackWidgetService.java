package com.example.shoji.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.pojo.Recipe;
import com.example.shoji.bakingapp.provider.RecipeContract;
import com.example.shoji.bakingapp.provider.RecipeProvider;
import com.example.shoji.bakingapp.ui.RecipeActivity;
import com.example.shoji.bakingapp.utils.RecipeProviderUtils;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    Cursor mCursor;

    private int mAppWidgetId;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        if (mCursor != null)
            mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;

        mCursor.moveToPosition(position);
        String name = mCursor.getString(mCursor.getColumnIndex(RecipeContract.COLUMN_NAME));
        String recipe_id = mCursor.getString(mCursor.getColumnIndex(RecipeContract.COLUMN_RECIPE_ID));
        String servings = mCursor.getString(mCursor.getColumnIndex(RecipeContract.COLUMN_SERVINGS));
        String image = mCursor.getString(mCursor.getColumnIndex(RecipeContract.COLUMN_IMAGE));

        Recipe recipe = new Recipe();
        recipe.setId(recipe_id);
        recipe.setName(name);
        recipe.setServings(servings);
        recipe.setImage(image);
        String _id = mCursor.getString(mCursor.getColumnIndex(RecipeContract._ID));
        recipe.setIngredientList(RecipeProviderUtils.getIngredientsFromDb(mContext, _id));
        recipe.setStepList(RecipeProviderUtils.getStepsFromDb(mContext, _id));


        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_list);

        // Put recipe as extra
        Bundle extras = new Bundle();
        extras.putParcelable(RecipeActivity.EXTRA_RECIPE_DATA, recipe);

        // FillInIntent
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        fillInIntent.setAction(RecipeActivity.ACTION_OPEN_INGREDIENT_LIST);
        views.setOnClickFillInIntent(R.id.appwidget_text, fillInIntent);
        views.setTextViewText(R.id.appwidget_text, recipe.getName());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
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

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                RecipeProvider.Recipes.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }
}
