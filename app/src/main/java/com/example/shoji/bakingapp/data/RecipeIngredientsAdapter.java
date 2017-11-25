package com.example.shoji.bakingapp.data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.pojo.Recipe;

public class RecipeIngredientsAdapter
        extends RecyclerView.Adapter<SimpleViewHolder> {

    private Recipe mRecipe;


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean attachToRoot = false;
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recipes_list_adapter_item, parent, attachToRoot);

        SimpleViewHolder.OnClickListener onClickHandler = null;

        return new SimpleViewHolder(itemView, R.id.recipe_adapter_title_tv, onClickHandler);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        //TODO get more
        String ingredient = mRecipe.getIngredientList().get(position).getDescription();
        holder.bindViewHolder(ingredient);

    }

    @Override
    public int getItemCount() {
        if(mRecipe != null && mRecipe.getIngredientList() != null)
            return mRecipe.getIngredientList().size();
        return 0;
    }


    public Recipe getRecipe() {
        return mRecipe;
    }

    public void setRecipe(Recipe recipe) {
        this.mRecipe = recipe;
    }


}
