package com.example.shoji.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.shoji.bakingapp.BuildConfig;
import com.example.shoji.bakingapp.IdlingResource.SimpleIdlingResource;
import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.backgroundtask.FetchRecipesListener;
import com.example.shoji.bakingapp.backgroundtask.QueryRecipesListener;
import com.example.shoji.bakingapp.data.RecipesListAdapter;
import com.example.shoji.bakingapp.pojo.Recipe;
import com.example.shoji.bakingapp.utils.BakerUtils;
import com.example.shoji.bakingapp.utils.NetworkUtils;

import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity
        extends AppCompatActivityEx
        implements FetchRecipesListener.OnLoadFinishedListener,
                    QueryRecipesListener.OnLoadFinishedListener,
                    RecipesListAdapter.OnClickListener {
    private static final String SAVE_INSTANCE_STATE_RECIPE_DATA = "recipe-data";
    private final static String SAVE_INSTANCE_STATE_LIST_POSITION = "list-position";

    private ProgressBar mProgressBar;

    private boolean mIsTabletMode;
    private RecipesListAdapter mRecipeListAdapter;
    private RecyclerView mRecipeListRecyclerView;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            if(BuildConfig.DEBUG)
                Timber.plant(new Timber.DebugTree());
            Timber.d("Logging With Timber");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* check if sw600dp layout was loaded. If so, it will enable table mode */
        mIsTabletMode = (findViewById(R.id.activity_main_sw600dp_layout) != null);

        mProgressBar = findViewById(R.id.activity_main_progressbar);
        createRecipesListRecyclerView();

        // Get the IdlingResource instance
        mIdlingResource = getIdlingResource();
        if(mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }

        if(containsSavedInstanceState(savedInstanceState)) {
            restoreListInstanceState(savedInstanceState);
        }
        else {
            mProgressBar.setVisibility(View.VISIBLE);
            if(NetworkUtils.isNetworkConnected(this)) {
                BakerUtils.fetchRecipes(this, getSupportLoaderManager(), this);
            }
            else {
                BakerUtils.queryRecipes(this, getSupportLoaderManager(), this);
            }
        }
    }

    private void createRecipesListRecyclerView() {
        Context context = this;

        mRecipeListRecyclerView = findViewById(R.id.activity_main_recipes_recycler_view);

        RecyclerView.LayoutManager layoutManager;

        if(mIsTabletMode) {
            int numColumns = getResources().getInteger(R.integer.activity_main_layout_num_columns);
            layoutManager = new GridLayoutManager(context, numColumns);
        }
        else {
            layoutManager = new LinearLayoutManager(context);
        }

        mRecipeListRecyclerView.setLayoutManager(layoutManager);

        mRecipeListRecyclerView.setHasFixedSize(true);

        RecipesListAdapter.OnClickListener onClickRecipeHandler = this;


        mRecipeListAdapter = new RecipesListAdapter(onClickRecipeHandler);
        mRecipeListRecyclerView.setAdapter(mRecipeListAdapter);

    }

    /* after json fetching is finished */
    @Override
    public void onFetchRecipesFinished() {
        BakerUtils.queryRecipes(this, getSupportLoaderManager(), this);
    }

    /* after db query is finished */
    @Override
    public void onQueryRecipesFinished(ArrayList<Recipe> result) {
        mProgressBar.setVisibility(View.INVISIBLE);

        if(result == null || result.size() == 0) {
            showSnackbar(R.id.activity_main_recipes_recycler_view,
                    R.string.error_no_network_short, Snackbar.LENGTH_LONG);
        }
        else
            swapAdapterData(result);

        if(mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
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

        if(mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
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

    @VisibleForTesting
    @NonNull
    public SimpleIdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }
}
