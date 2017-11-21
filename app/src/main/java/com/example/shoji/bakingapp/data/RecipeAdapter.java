package com.example.shoji.bakingapp.data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.pojo.Recipe;

import java.util.ArrayList;

import timber.log.Timber;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeViewHolder>
        implements RecipeViewHolder.OnClickRecipeListener {

    private ArrayList<Recipe> mRecipeList;
    private OnClickRecipeListener mOnClickHandler;




    public interface OnClickRecipeListener {
        void onClickRecipe(Recipe recipe);
    }

    public RecipeAdapter(OnClickRecipeListener onClickRecipeListener) {
        mOnClickHandler = onClickRecipeListener;
    }




    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean attachToRoot = false;
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recipe_adapter_item, parent, attachToRoot);

        RecipeViewHolder.OnClickRecipeListener onClickHandler = this;

        return new RecipeViewHolder(itemView, onClickHandler);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = mRecipeList.get(position);
        holder.bindViewHolder(recipe);

    }

    @Override
    public int getItemCount() {
        if(mRecipeList != null)
            return mRecipeList.size();
        return 0;
    }


    public ArrayList<Recipe> getRecipeList() {
        return mRecipeList;
    }

    public void setRecipeList(ArrayList<Recipe> recipeList) {
        this.mRecipeList = recipeList;
    }



    @Override
    public void onClickRecipe(int position) {
        Recipe recipe = mRecipeList.get(position);
        Timber.d("TAPPED ON POSITION: %d", position);
        mOnClickHandler.onClickRecipe(recipe);
    }
}
