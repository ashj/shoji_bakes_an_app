package com.example.shoji.bakingapp.data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.pojo.Recipe;

import java.util.ArrayList;

import timber.log.Timber;

public abstract class AbstractRecipesListAdapter
        extends RecyclerView.Adapter<SimpleViewHolder>
        implements SimpleViewHolder.OnClickListener {

    protected OnClickListener mOnClickHandler;


    public interface OnClickListener {
        void onClick(Recipe recipe);
    }


    public AbstractRecipesListAdapter(OnClickListener onClickRecipeListener) {
        mOnClickHandler = onClickRecipeListener;
    }

}
