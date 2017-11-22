package com.example.shoji.bakingapp.data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.pojo.Recipe;

import java.util.ArrayList;

import timber.log.Timber;

public class RecipesListAdapter
        extends RecyclerView.Adapter<SimpleViewHolder>
        implements SimpleViewHolder.OnClickListener  {

    private ArrayList<Recipe> mRecipeList;

    protected OnClickListener mOnClickHandler;


    public interface OnClickListener {
        void onClick(Recipe recipe);
    }




    public RecipesListAdapter(OnClickListener onClickRecipeListener) {
        mOnClickHandler = onClickRecipeListener;
    }


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean attachToRoot = false;
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recipe_adapter_item, parent, attachToRoot);

        SimpleViewHolder.OnClickListener onClickHandler = this;

        return new SimpleViewHolder(itemView, R.id.recipe_adapter_title_tv, onClickHandler);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        Recipe recipe = mRecipeList.get(position);
        holder.bindViewHolder(recipe.getName());

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
    public void onClick(int position) {
        Recipe recipe = mRecipeList.get(position);
        Timber.d("Tapped on position: %d", position);
        mOnClickHandler.onClick(recipe);
    }
}
