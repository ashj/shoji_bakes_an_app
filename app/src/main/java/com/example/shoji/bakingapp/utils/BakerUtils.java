package com.example.shoji.bakingapp.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;


import com.example.shoji.bakingapp.backgroundtask.FetchRecipesListener;
import com.example.shoji.bakingapp.backgroundtask.LoaderCallBacksEx;
import com.example.shoji.bakingapp.backgroundtask.LoaderID;
import com.example.shoji.bakingapp.backgroundtask.QueryRecipesListener;
import com.example.shoji.bakingapp.pojo.Recipe;

import java.util.ArrayList;


public class BakerUtils {

    synchronized public static void fetchRecipes(Context context,
                                                 LoaderManager loaderManager,
                                                 FetchRecipesListener.OnLoadFinishedListener onLoadFinishedHandler) {
        int loaderId = LoaderID.LOADER_ID_FETCH_RECIPES;
        Bundle args = null;

        FetchRecipesListener fetchRecipesHandler = new FetchRecipesListener(onLoadFinishedHandler);

        LoaderCallBacksEx<Void> recipeLoaderCallBacks =
                new LoaderCallBacksEx<>(context, fetchRecipesHandler);

        LoaderCallBackUtils.initOrRestartLoader(
                loaderId,
                args,
                loaderManager,
                recipeLoaderCallBacks);
    }

    synchronized public static void queryRecipes(Context context,
                                                 LoaderManager loaderManager,
                                                 QueryRecipesListener.OnLoadFinishedListener onLoadFinishedHandler) {
        int loaderId = LoaderID.LOADER_ID_FETCH_RECIPES;
        Bundle args = null;

        QueryRecipesListener queryRecipesHandler = new QueryRecipesListener(onLoadFinishedHandler);

        LoaderCallBacksEx<ArrayList<Recipe>> recipeLoaderCallBacks =
                new LoaderCallBacksEx<>(context, queryRecipesHandler);

        LoaderCallBackUtils.initOrRestartLoader(
                loaderId,
                args,
                loaderManager,
                recipeLoaderCallBacks);
    }


}
