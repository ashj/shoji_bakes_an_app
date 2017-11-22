package com.example.shoji.bakingapp.ui;

import android.app.Activity;
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
import com.example.shoji.bakingapp.data.RecipeMasterListAdapter;
import com.example.shoji.bakingapp.pojo.Recipe;

import timber.log.Timber;

public class RecipeMasterListFragment extends Fragment {

    private Recipe mRecipe;

    private RecipeMasterListAdapter mRecipeMasterListAdapter;
    private RecipeMasterListAdapter.OnClickListener mOnClickHandler;
    private RecyclerView mRecipeRecyclerView;




    public RecipeMasterListFragment() {}


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        boolean attachToRoot = false;
        View rootView = inflater.inflate(
                R.layout.fragment_recipe_master_list,
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

        mRecipeMasterListAdapter =
                new RecipeMasterListAdapter(context, mOnClickHandler);
        mRecipeMasterListAdapter.setRecipe(mRecipe);


        mRecipeRecyclerView = rootView.findViewById(R.id.fragment_recipe_master_list_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecipeRecyclerView.setLayoutManager(linearLayoutManager);

        mRecipeRecyclerView.setHasFixedSize(true);

        mRecipeRecyclerView.setAdapter(mRecipeMasterListAdapter);

        mRecipeMasterListAdapter.notifyDataSetChanged();

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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnClickHandler = (RecipeMasterListAdapter.OnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }
}
