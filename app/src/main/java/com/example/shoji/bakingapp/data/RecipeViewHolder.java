package com.example.shoji.bakingapp.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.pojo.Recipe;

import timber.log.Timber;

public class RecipeViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private Recipe mRecipe;
    private OnClickRecipeListener mOnClickHandler;

    private TextView mRecipeTitle;

    interface OnClickRecipeListener {
        void onClickRecipe(int position);
    }

    public RecipeViewHolder(View itemView,
                            OnClickRecipeListener onClickHandler) {
        super(itemView);
        mOnClickHandler = onClickHandler;

        mRecipeTitle = itemView.findViewById(R.id.recipe_adapter_title_tv);

        View.OnClickListener viewOnClickListener = this;
        mRecipeTitle.setOnClickListener(viewOnClickListener);

    }

    public void bindViewHolder(Recipe recipe) {
        mRecipe = recipe;
        // set view here
        mRecipeTitle.setText(recipe.getName());

    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        Timber.d("TAPPED ON POSITION (holder): %d", position);
        mOnClickHandler.onClickRecipe(position);
    }
}
