package com.example.shoji.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.shoji.bakingapp.ui.AppCompatActivityEx;
import com.example.shoji.bakingapp.ui.RecipeActivity;
import com.example.shoji.bakingapp.utils.BakerUtils;
import com.example.shoji.bakingapp.utils.NetworkUtils;

import java.util.ArrayList;

import timber.log.Timber;

public class WidgetIngredientListConfigurationActivity        extends AppCompatActivityEx
        implements
        QueryRecipesListener.OnLoadFinishedListener,
        RecipesListAdapter.OnClickListener {
    private static final String SAVE_INSTANCE_STATE_RECIPE_DATA = "recipe-data";
    private final static String SAVE_INSTANCE_STATE_LIST_POSITION = "list-position";

    private ProgressBar mProgressBar;

    private RecipesListAdapter mRecipeListAdapter;
    private RecyclerView mRecipeListRecyclerView;

    private int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_ingredient_list_configuration);

        mProgressBar = findViewById(R.id.activity_widget_ingredient_list_configuration_progressbar);
        createRecipesListRecyclerView();

        getAppWidgetId();
        if(mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
            finish();

        if(containsSavedInstanceState(savedInstanceState)) {
            restoreListInstanceState(savedInstanceState);
        }
        else {
            mProgressBar.setVisibility(View.VISIBLE);
                BakerUtils.queryRecipes(this, getSupportLoaderManager(), this);
        }
    }

    private void getAppWidgetId() {
        mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Intent intent = getIntent();
        if(intent == null)
            return;
        Bundle extras = intent.getExtras();
        if(extras == null || !extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID))
            return;

        mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
        Timber.d("AppWidgetId: %d", mAppWidgetId);
    }

    private void createRecipesListRecyclerView() {
        Context context = this;

        mRecipeListRecyclerView = findViewById(R.id.activity_widget_ingredient_list_configuration_recipes_recycler_view);

        RecyclerView.LayoutManager layoutManager;

        layoutManager = new LinearLayoutManager(context);

        mRecipeListRecyclerView.setLayoutManager(layoutManager);

        mRecipeListRecyclerView.setHasFixedSize(true);

        RecipesListAdapter.OnClickListener onClickRecipeHandler = this;


        mRecipeListAdapter = new RecipesListAdapter(onClickRecipeHandler);
        mRecipeListRecyclerView.setAdapter(mRecipeListAdapter);

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

        Intent intentRefresh = new Intent(this, BakerAppWidgetService.class);
        intentRefresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intentRefresh.putExtra(WidgetIngredientList.EXTRA_RECIPE_ID, recipe.getId());
        intentRefresh.putExtra(WidgetIngredientList.EXTRA_RECIPE_NAME, recipe.getName());
        BakerAppWidgetService.startRefreshWidget(this, intentRefresh);


        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intent.putExtra(WidgetIngredientList.EXTRA_RECIPE_ID, recipe.getId());
        intentRefresh.putExtra(WidgetIngredientList.EXTRA_RECIPE_NAME, recipe.getName());

        setResult(RESULT_OK, intent);
        finish();
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

