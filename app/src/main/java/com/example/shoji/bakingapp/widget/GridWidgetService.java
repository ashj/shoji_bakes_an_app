package com.example.shoji.bakingapp.widget;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.app.PendingIntent;
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
import com.example.shoji.bakingapp.ui.MainActivity;
import com.example.shoji.bakingapp.ui.RecipeActivity;
import com.example.shoji.bakingapp.utils.RecipeProviderUtils;

import timber.log.Timber;


public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;

    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
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
        Timber.d("onDataSetChanged");
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            Timber.d("SHOJI Cursor is null, returning 0");
            return 0;
        }
        Timber.d("SHOJI Cursor size: %d", mCursor.getCount());
        return mCursor.getCount();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        Timber.d("getViewAt -- entry");
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

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.baker_app_widget);

        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        Bundle extras = new Bundle();
        //todo landscape mode
        extras.putParcelable(RecipeActivity.EXTRA_RECIPE_DATA, recipe);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.appwidget_text, fillInIntent);
        views.setTextViewText(R.id.appwidget_text, recipe.getName());

        Timber.d("getViewAt");
//        CharSequence widgetText = mContext.getString(R.string.appwidget_text);
//        //RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.baker_app_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
//
//        Intent intent = new Intent(mContext, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
//        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
