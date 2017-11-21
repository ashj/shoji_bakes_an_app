package com.example.shoji.bakingapp.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.pojo.Recipe;

import java.util.ArrayList;

import timber.log.Timber;

public class RecipeMasterListAdapter
        extends AbstractRecipesListAdapter  {
    private static final int ITEMVIEWTYPE_RECIPE_INGREDIENT = 0;
    private static final int ITEMVIEWTYPE_RECIPE_STEP = 1;
    private static final int ITEMVIEWTYPE_UNKNOWN = -1;

    private Context mContext;
    private Recipe mRecipe;


    public RecipeMasterListAdapter(Context context,
                                   OnClickListener onClickRecipeListener) {
        super(onClickRecipeListener);
        mContext = context;
    }



    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.d("onCreateViewHolder");
        boolean attachToRoot = false;
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recipe_adapter_item, parent, attachToRoot);

        SimpleViewHolder.OnClickListener onClickHandler = this;

        return new SimpleViewHolder(itemView, R.id.recipe_adapter_title_tv, onClickHandler);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch(viewType) {
            case ITEMVIEWTYPE_RECIPE_INGREDIENT:
                Timber.d("onBindViewHolder, pos %d, ITEMVIEWTYPE_RECIPE_INGREDIENT", position);
                holder.bindViewHolder(mContext.getString(R.string.ingredients_list));
                break;
            case ITEMVIEWTYPE_RECIPE_STEP:
                int offsetPosition = position - 1;
                if(mRecipe.getIngredientList() == null || mRecipe.getIngredientList().size() == 0){
                    offsetPosition = position;
                }
                Timber.d("onBindViewHolder, pos %d/ offset: %d, ITEMVIEWTYPE_RECIPE_STEP",
                        position, offsetPosition);

                holder.bindViewHolder(mRecipe.getStepList().get(offsetPosition).getShortDescription());
                Timber.d(mRecipe.getStepList().get(offsetPosition).getShortDescription());
                break;
            default:
                break;
        }

    }

    @Override
    public int getItemCount() {
        int totalCount = 0;
        if(mRecipe != null) {
            if(mRecipe.getIngredientList() != null &&
                    mRecipe.getIngredientList().size() != 0) {
            /* use just 1 position instead of list size */
                totalCount += 1;
            }
            if(mRecipe.getStepList() != null) {
                totalCount += mRecipe.getStepList().size();
            }
        }

        return totalCount;
    }


    public Recipe getRecipe() {
        return mRecipe;
    }

    public void setRecipe(Recipe recipe) {
        this.mRecipe = recipe;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = ITEMVIEWTYPE_UNKNOWN;
        int startingStepPosition = 1;

        int numIngredients = 0;
        if(mRecipe.getIngredientList() != null &&
                mRecipe.getIngredientList().size() != 0) {
            /* use just 1 position instead of list size */
            numIngredients = 1;
        }
        else {
            /* no ingredient list, shift steps position */
            startingStepPosition = 0;
        }

        int numSteps = 0;
        if(mRecipe.getStepList() != null) {
            numSteps = mRecipe.getStepList().size();
        }

        if((numIngredients == 1) && (position == 0)) {
            Timber.d("getItemViewType pos: %d - ITEMVIEWTYPE_RECIPE_INGREDIENT", position);
            viewType = ITEMVIEWTYPE_RECIPE_INGREDIENT;
        }
        else if((numSteps != 0) && (startingStepPosition <= position) && (position <= numSteps)) {
            Timber.d("getItemViewType pos %d - ITEMVIEWTYPE_RECIPE_STEP", position);
            viewType = ITEMVIEWTYPE_RECIPE_STEP;
        }

        return viewType;
    }

    @Override
    public void onClick(int position) {
        Timber.d("Tapped on recipe: %s", mRecipe.getName());
        mOnClickHandler.onClick(mRecipe);
    }
}
