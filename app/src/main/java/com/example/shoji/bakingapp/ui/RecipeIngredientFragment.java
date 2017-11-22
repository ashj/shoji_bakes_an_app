package com.example.shoji.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.data.RecipeIngredientsAdapter;
import com.example.shoji.bakingapp.data.RecipeMasterListAdapter;
import com.example.shoji.bakingapp.pojo.Recipe;

import timber.log.Timber;


public class RecipeIngredientFragment extends Fragment {

    private Recipe mRecipe;

    private RecipeIngredientsAdapter mRecipeIngredientsAdapter;
    private RecyclerView mIngredientRecyclerView;

    public RecipeIngredientFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        boolean attachToRoot = false;
        View rootView = inflater.inflate(
                R.layout.fragment_recipe_ingredient_list,
                container,
                attachToRoot);

        mRecipe = getRecipeFromActivity();

        createRecyclerView(rootView);

        FragmentActivity activity = getActivity();
        if (activity != null)
            activity.setTitle(mRecipe.getName());

        return rootView;
    }


    private void createRecyclerView(View rootView) {
        Timber.d("createRecyclerView");
        Context context = getContext();

        mRecipeIngredientsAdapter =
                new RecipeIngredientsAdapter();
        mRecipeIngredientsAdapter.setRecipe(mRecipe);


        mIngredientRecyclerView = rootView.findViewById(R.id.fragment_recipe_ingredient_list_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mIngredientRecyclerView.setLayoutManager(linearLayoutManager);

        mIngredientRecyclerView.setHasFixedSize(true);

        mIngredientRecyclerView.setAdapter(mRecipeIngredientsAdapter);

        mRecipeIngredientsAdapter.notifyDataSetChanged();

    }

    private Recipe getRecipeFromActivity() {
        FragmentActivity activity = getActivity();
        if(activity == null)
            return null;

        Intent intent = activity.getIntent();
        if(intent == null)
            return null;

        if(!intent.hasExtra(RecipeActivity.EXTRA_RECIPE_DATA))
            return null;

        return intent.getParcelableExtra(RecipeActivity.EXTRA_RECIPE_DATA);
    }
}
