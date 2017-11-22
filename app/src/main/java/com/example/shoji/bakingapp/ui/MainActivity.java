package com.example.shoji.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.shoji.bakingapp.BuildConfig;
import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.backgroundtask.FetchRecipesListener;
import com.example.shoji.bakingapp.data.RecipesListAdapter;
import com.example.shoji.bakingapp.pojo.Recipe;
import com.example.shoji.bakingapp.utils.BakerUtils;

import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity
        extends AppCompatActivityEx
        implements FetchRecipesListener.OnLoadFinishedListener,
                    RecipesListAdapter.OnClickListener {
    private static final String SAVE_INSTANCE_STATE_RECIPE_DATA = "recipe-data";
    private final static String SAVE_INSTANCE_STATE_LIST_POSITION = "list-position";


    private RecipesListAdapter mRecipeListAdapter;
    private RecyclerView mRecipeListRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            if(BuildConfig.DEBUG)
                Timber.plant(new Timber.DebugTree());
            Timber.d("Logging With Timber");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createRecipesListRecyclerView();

        if(containsSavedInstanceState(savedInstanceState)) {
            restoreListInstanceState(savedInstanceState);
        }
        else {
            BakerUtils.fetchRecipes(this, getSupportLoaderManager(), this);
        }
    }

    private void createRecipesListRecyclerView() {
        Context context = this;

        mRecipeListRecyclerView = findViewById(R.id.activity_main_recipes_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecipeListRecyclerView.setLayoutManager(linearLayoutManager);

        mRecipeListRecyclerView.setHasFixedSize(true);

        RecipesListAdapter.OnClickListener onClickRecipeHandler = this;


        mRecipeListAdapter = new RecipesListAdapter(onClickRecipeHandler);
        mRecipeListRecyclerView.setAdapter(mRecipeListAdapter);

    }

    @Override
    public void onFetchRecipesFinished(ArrayList<Recipe> result) {
        swapAdapterData(result);
    }

    public void swapAdapterData(ArrayList<Recipe> newRecipeList) {
        mRecipeListAdapter.setRecipeList(newRecipeList);
        mRecipeListAdapter.notifyDataSetChanged();
    }

    /* Checks and restore previous data */
    private boolean containsSavedInstanceState(Bundle savedInstanceState) {
        boolean result = false;
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(SAVE_INSTANCE_STATE_RECIPE_DATA)) {
                ArrayList<Recipe> savedRecipeList =
                        savedInstanceState.getParcelableArrayList(SAVE_INSTANCE_STATE_RECIPE_DATA);
                swapAdapterData(savedRecipeList);
                result = true;
            }
        }

        return result;
    }
    private void restoreListInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.containsKey(SAVE_INSTANCE_STATE_LIST_POSITION)) {
            Parcelable listState = savedInstanceState
                    .getParcelable(SAVE_INSTANCE_STATE_LIST_POSITION);
            mRecipeListRecyclerView.getLayoutManager()
                    .onRestoreInstanceState(listState);
            }
    }

    @Override
    public void onClick(Recipe recipe) {
        Timber.d("TAPPED ON: %s", recipe.getName());

        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(RecipeActivity.EXTRA_RECIPE_DATA, recipe);

        startActivity(intent);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SAVE_INSTANCE_STATE_LIST_POSITION,
                mRecipeListRecyclerView.getLayoutManager()
                        .onSaveInstanceState());
        outState.putParcelableArrayList(SAVE_INSTANCE_STATE_RECIPE_DATA,
                mRecipeListAdapter.getRecipeList());
    }

}
