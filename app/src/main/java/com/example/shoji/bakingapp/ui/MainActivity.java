package com.example.shoji.bakingapp.ui;

import android.os.Bundle;

import com.example.shoji.bakingapp.BuildConfig;
import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.backgroundtask.FetchRecipesListener;
import com.example.shoji.bakingapp.utils.BakerUtils;
import com.example.shoji.bakingapp.utils.RecipeJsonUtils;

import timber.log.Timber;

public class MainActivity
        extends AppCompatActivityEx
        implements FetchRecipesListener.OnLoadFinishedListener{

    private String mRecipesJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
        Timber.d("Logging With Timber");

        BakerUtils.fetchRecipes(this, getSupportLoaderManager(), this);

    }



    @Override
    public void onFetchRecipesFinished(String recipeJsonString) {
        mRecipesJson = recipeJsonString;
        Timber.d(recipeJsonString);

        RecipeJsonUtils.listRecipes(mRecipesJson);
    }
}
