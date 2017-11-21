package com.example.shoji.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.pojo.Recipe;

import timber.log.Timber;

public class RecipeMasterListFragment extends Fragment {

    private Recipe mRecipe;

    public RecipeMasterListFragment () {}

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

        TextView title = rootView.findViewById(R.id.fragment_recipe_master_list_recipe_name);

        Recipe recipe = getRecipeFromActivity();

        getActivity().setTitle(recipe.getName());
        title.setText(recipe.getName());

        Timber.d("onCreateView -- doing nothing for now");
        return rootView;
    }

    private Recipe getRecipeFromActivity() {
        FragmentActivity activity = getActivity();
        if(activity == null)
            return null;

        Intent intent = activity.getIntent();
        if(intent == null)
            return null;

        if(!intent.hasExtra(RecipeMasterListActivity.EXTRA_RECIPE_DATA))
            return null;

        return intent.getParcelableExtra(RecipeMasterListActivity.EXTRA_RECIPE_DATA);
    }

}
