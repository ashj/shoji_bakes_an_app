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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.pojo.Recipe;
import com.example.shoji.bakingapp.pojo.RecipeIngredient;
import com.example.shoji.bakingapp.provider.RecipeContract;
import com.example.shoji.bakingapp.provider.RecipeIngredientContract;
import com.example.shoji.bakingapp.provider.RecipeIngredientProvider;
import com.example.shoji.bakingapp.provider.RecipeProvider;
import com.example.shoji.bakingapp.ui.RecipeActivity;
import com.example.shoji.bakingapp.utils.RecipeProviderUtils;

import timber.log.Timber;


public class IngredientGridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientGridRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class IngredientGridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private String mRecipeId;
    private String mRecipeName;
    private Context mContext;

    private Cursor mCursor;

    public IngredientGridRemoteViewsFactory(Context applicationContext,
                                            Intent intent) {
        mContext = applicationContext;

        mRecipeId = intent.getStringExtra(
                WidgetIngredientList.EXTRA_RECIPE_ID);

        mRecipeName = intent.getStringExtra(
                WidgetIngredientList.EXTRA_RECIPE_NAME);

        Timber.d("IngredientGridRemoteViewsFactory with recipeId: %s - %s", mRecipeId, mRecipeName);
    }

    @Override
    public void onCreate() {

    }


    @Override
    public void onDataSetChanged() {
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                RecipeIngredientProvider.Ingredients.CONTENT_URI,
                null,
                RecipeIngredientContract.COLUMN_RECIPE_ID + "=" + mRecipeId,
                null,
                null
        );
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
        return mCursor.getCount() + 1;
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;
        String formattedText;
        mCursor.moveToPosition(position);
        if(position == 0) {
            formattedText = mRecipeName;
        }
        else {
            mCursor.moveToPosition(position - 1);
            String quantity = mCursor.getString(mCursor.getColumnIndex(RecipeIngredientContract.COLUMN_QUANTITY));
            String measure = mCursor.getString(mCursor.getColumnIndex(RecipeIngredientContract.COLUMN_MEASURE));
            String description = mCursor.getString(mCursor.getColumnIndex(RecipeIngredientContract.COLUMN_DESCRIPTION));

            formattedText = mContext.getString(R.string.recipe_formatted_ingredients,
                    quantity, measure, description);
        }
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_list);
        views.setTextViewText(R.id.appwidget_text, formattedText);

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

