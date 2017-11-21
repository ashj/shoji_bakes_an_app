package com.example.shoji.bakingapp.backgroundtask;

import android.content.Context;
import android.os.Bundle;

import com.example.shoji.bakingapp.utils.NetworkUtils;

import timber.log.Timber;

public class FetchRecipesListener
        implements LoaderCallBacksListenersInterface<String> {
    //private static String RECIPES_URL = "http://go.udacity.com/android-baking-app-json";
    private static String RECIPES_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private OnLoadFinishedListener mOnLoadFinishedHandler;

    /* Enable a listener to process the result. */
    public interface OnLoadFinishedListener {
        void onFetchRecipesFinished(String recipeJsonString);
    }

    public FetchRecipesListener(OnLoadFinishedListener onLoadFinishedHandler) {
        mOnLoadFinishedHandler = onLoadFinishedHandler;
    }

    @Override
    public void onStartLoading(Context context) { }

    @Override
    public String onLoadInBackground(Context context, Bundle args) {
        String result = NetworkUtils.getDataFromUrlString(RECIPES_URL);
        //Timber.d("FetchRecipesListener -- got json: %s", result);
        return result;
    }

    @Override
    public void onLoadFinished(Context context, String result) {
        /* The listener will process the result here. */
        mOnLoadFinishedHandler.onFetchRecipesFinished(result);
    }
}
