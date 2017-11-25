package com.example.shoji.bakingapp.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.pojo.Recipe;

import timber.log.Timber;

public class RecipeMasterListAdapter
        extends RecyclerView.Adapter<SimpleViewHolder>
        implements SimpleViewHolder.OnClickListener
{
    private static final int ITEMVIEWTYPE_RECIPE_INGREDIENT = 0;
    private static final int ITEMVIEWTYPE_RECIPE_STEP = 1;
    private static final int ITEMVIEWTYPE_UNKNOWN = -1;

    private Context mContext;
    private Recipe mRecipe;

    protected OnClickListener mOnClickHandler;


    public interface OnClickListener {
        void onClickIngredient();
        void onClickStep(int position);
    }

    public RecipeMasterListAdapter(Context context,
                                   OnClickListener onClickRecipeListener) {
        mOnClickHandler = onClickRecipeListener;
        mContext = context;
    }



    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.d("onCreateViewHolder");
        boolean attachToRoot = false;
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recipe_details_adapter_item, parent, attachToRoot);

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
                int positionOffset = getPositionOffset(position);
                Timber.d("onBindViewHolder, pos %d/ offset: %d, ITEMVIEWTYPE_RECIPE_STEP",
                        position, positionOffset);

                holder.bindViewHolder(mRecipe.getStepList().get(positionOffset).getShortDescription());
                Timber.d(mRecipe.getStepList().get(positionOffset).getShortDescription());
                break;
            default:
                break;
        }

    }


    private int getPositionOffset(int position) {
        int offsetPosition = position - 1;
        if(mRecipe.getIngredientList() == null || mRecipe.getIngredientList().size() == 0){
            offsetPosition = position;
        }
        return offsetPosition;
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

        switch (getItemViewType(position)) {
            case ITEMVIEWTYPE_RECIPE_INGREDIENT:
                Timber.d("Tapped on ingredient list: %d", position);
                mOnClickHandler.onClickIngredient();
                break;
            case ITEMVIEWTYPE_RECIPE_STEP:
                int positionOffset = getPositionOffset(position);
                Timber.d("Tapped on step list: %d - offset: %d", position, positionOffset);
                mOnClickHandler.onClickStep(positionOffset);
                break;
        }
    }
}
