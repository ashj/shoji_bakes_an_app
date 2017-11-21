package com.example.shoji.bakingapp.ui;

import android.os.Bundle;

import com.example.shoji.bakingapp.BuildConfig;
import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.backgroundtask.FetchRecipesListener;
import com.example.shoji.bakingapp.pojo.Recipe;
import com.example.shoji.bakingapp.utils.BakerUtils;

import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity
        extends AppCompatActivityEx
        implements FetchRecipesListener.OnLoadFinishedListener{

    private ArrayList<Recipe> mRecipesList;


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
    public void onFetchRecipesFinished(ArrayList<Recipe> result) {
        mRecipesList = result;
        //Timber.d(recipeJsonString);

        for(int i = 0; i < mRecipesList.size(); i++)
            Timber.d((mRecipesList.get(i).toString()));

    }
}
