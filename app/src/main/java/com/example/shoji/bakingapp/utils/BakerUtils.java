package com.example.shoji.bakingapp.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;


import com.example.shoji.bakingapp.backgroundtask.FetchRecipesListener;
import com.example.shoji.bakingapp.backgroundtask.LoaderCallBacksEx;
import com.example.shoji.bakingapp.backgroundtask.LoaderID;


public class BakerUtils {

    synchronized public static void fetchRecipes(Context context,
                                                 LoaderManager loaderManager,
                                                 FetchRecipesListener.OnLoadFinishedListener onLoadFinishedHandler) {
        int loaderId = LoaderID.LOADER_ID_FETCH_RECIPES;
        Bundle args = null;

        FetchRecipesListener fetchRecipesHandler = new FetchRecipesListener(onLoadFinishedHandler);

        LoaderCallBacksEx<String> recipeLoaderCallBacks =
                new LoaderCallBacksEx<>(context, fetchRecipesHandler);

        LoaderCallBackUtils.initOrRestartLoader(
                loaderId,
                args,
                loaderManager,
                recipeLoaderCallBacks);
    }
}
