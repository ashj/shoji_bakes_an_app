package com.example.shoji.bakingapp.backgroundtask;

import android.content.Context;
import android.os.Bundle;

import com.example.shoji.bakingapp.pojo.Recipe;
import com.example.shoji.bakingapp.utils.NetworkUtils;
import com.example.shoji.bakingapp.utils.RecipeJsonUtils;
import com.example.shoji.bakingapp.utils.RecipeProviderUtils;

import java.util.ArrayList;


public class QueryRecipesListener
        implements LoaderCallBacksListenersInterface<ArrayList<Recipe>> {

    private OnLoadFinishedListener mOnLoadFinishedHandler;

    /* Enable a listener to process the result. */
    public interface OnLoadFinishedListener {
        void onQueryRecipesFinished(ArrayList<Recipe> result);
    }

    public QueryRecipesListener(OnLoadFinishedListener onLoadFinishedHandler) {
        mOnLoadFinishedHandler = onLoadFinishedHandler;
    }

    @Override
    public void onStartLoading(Context context) { }

    @Override
    public ArrayList<Recipe> onLoadInBackground(Context context, Bundle args) {
        return RecipeProviderUtils.getRecipesFromDb(context);
    }

    @Override
    public void onLoadFinished(Context context, ArrayList<Recipe> result) {
        /* The listener will process the result here. */
        mOnLoadFinishedHandler.onQueryRecipesFinished(result);
    }
}