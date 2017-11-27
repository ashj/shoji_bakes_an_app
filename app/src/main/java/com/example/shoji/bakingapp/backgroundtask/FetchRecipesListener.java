package com.example.shoji.bakingapp.backgroundtask;

import android.content.Context;
import android.os.Bundle;

import com.example.shoji.bakingapp.pojo.Recipe;
import com.example.shoji.bakingapp.utils.NetworkUtils;
import com.example.shoji.bakingapp.utils.RecipeJsonUtils;
import com.example.shoji.bakingapp.utils.RecipeProviderUtils;

import java.util.ArrayList;

public class FetchRecipesListener
        implements LoaderCallBacksListenersInterface<Void> {
    //private static String RECIPES_URL = "http://go.udacity.com/android-baking-app-json";
    private static String RECIPES_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private OnLoadFinishedListener mOnLoadFinishedHandler;

    /* Enable a listener to process the result. */
    public interface OnLoadFinishedListener {
        void onFetchRecipesFinished();
    }

    public FetchRecipesListener(OnLoadFinishedListener onLoadFinishedHandler) {
        mOnLoadFinishedHandler = onLoadFinishedHandler;
    }

    @Override
    public void onStartLoading(Context context) { }

    @Override
    public Void onLoadInBackground(Context context, Bundle args) {
        String jsonString = NetworkUtils.getDataFromUrlString(RECIPES_URL);
        //Timber.d("FetchRecipesListener -- got json: %s", result);

        if(jsonString != null) {
            RecipeJsonUtils.listRecipes(context, jsonString);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Context context, Void o) {
        /* The listener will process the result here. */
        mOnLoadFinishedHandler.onFetchRecipesFinished();
    }
}
