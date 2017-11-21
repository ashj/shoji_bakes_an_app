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

    private OnClickListener mOnClickHandler;

    private TextView mTitle;

    interface OnClickListener {
        void onClick(int position);
    }

    public RecipeViewHolder(View itemView,
                            OnClickListener onClickHandler) {
        super(itemView);
        mOnClickHandler = onClickHandler;

        mTitle = itemView.findViewById(R.id.recipe_adapter_title_tv);

        View.OnClickListener viewOnClickListener = this;
        mTitle.setOnClickListener(viewOnClickListener);

    }

    public void bindViewHolder(Recipe recipe) {
        // set view here
        mTitle.setText(recipe.getName());

    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        Timber.d("TAPPED ON POSITION (holder): %d", position);
        mOnClickHandler.onClick(position);
    }
}
