package com.example.shoji.bakingapp.backgroundtask;

import android.content.Context;
import android.os.Bundle;

import com.example.shoji.bakingapp.pojo.Recipe;
import com.example.shoji.bakingapp.utils.NetworkUtils;
import com.example.shoji.bakingapp.utils.RecipeJsonUtils;

import java.util.ArrayList;

import timber.log.Timber;

public class FetchRecipesListener
        implements LoaderCallBacksListenersInterface<ArrayList<Recipe>> {
    //private static String RECIPES_URL = "http://go.udacity.com/android-baking-app-json";
    private static String RECIPES_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private OnLoadFinishedListener mOnLoadFinishedHandler;

    /* Enable a listener to process the result. */
    public interface OnLoadFinishedListener {
        void onFetchRecipesFinished(ArrayList<Recipe> result);
    }

    public FetchRecipesListener(OnLoadFinishedListener onLoadFinishedHandler) {
        mOnLoadFinishedHandler = onLoadFinishedHandler;
    }

    @Override
    public void onStartLoading(Context context) { }

    @Override
    public ArrayList<Recipe> onLoadInBackground(Context context, Bundle args) {
        String jsonString = NetworkUtils.getDataFromUrlString(RECIPES_URL);
        //Timber.d("FetchRecipesListener -- got json: %s", result);
        ArrayList<Recipe> result = null;

        if(jsonString != null)
            result = RecipeJsonUtils.listRecipes(jsonString);

        if(result != null) {
            RecipeJsonUtils.insertRecipesToDb(context, result);
        }
        return result;
    }

    @Override
    public void onLoadFinished(Context context, ArrayList<Recipe> result) {
        /* The listener will process the result here. */
        mOnLoadFinishedHandler.onFetchRecipesFinished(result);
    }
}
