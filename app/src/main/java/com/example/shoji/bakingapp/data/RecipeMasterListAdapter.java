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
    private static final int ITEMVIEWTYPE_RECIPE_SERVINGS = 2;
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
        final int INVALID = -1;
        int layoutResId = INVALID;
        switch (viewType) {
            case ITEMVIEWTYPE_RECIPE_SERVINGS:
                layoutResId = R.layout.recipe_serving_adapter_item;
                break;
            case ITEMVIEWTYPE_RECIPE_INGREDIENT:
            case ITEMVIEWTYPE_RECIPE_STEP:
                layoutResId = R.layout.recipe_details_adapter_item;
                break;
                default:
                    break;
        }
        SimpleViewHolder viewHolder = null;
        if(layoutResId != INVALID) {
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(layoutResId, parent, attachToRoot);
            SimpleViewHolder.OnClickListener onClickHandler = this;
            viewHolder = new SimpleViewHolder(itemView, R.id.recipe_adapter_title_tv, onClickHandler);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch(viewType) {
            case ITEMVIEWTYPE_RECIPE_SERVINGS:
                String text = mContext.getString(R.string.recipe_servings,mRecipe.getServings());
                Timber.d("onBindViewHolder, pos %d, ITEMVIEWTYPE_RECIPE_SERVINGS", position);
                holder.bindViewHolder(text);
                break;

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
        int offsetPosition = position - 2; // count servings
        if(mRecipe.getIngredientList() == null || mRecipe.getIngredientList().size() == 0){
            offsetPosition = position - 1; //discount ingredients
        }
        return offsetPosition;
    }

    @Override
    public int getItemCount() {
        int totalCount = 1; // count servings
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
        int serving_startPos = 0;
        int serving_endPos = serving_startPos + 1;

        int ingredients_startPos = serving_endPos;
        int ingredients_endPos;

        int steps_startPos;
        int steps_endPos;

        ingredients_endPos = ingredients_startPos;
        if(mRecipe.getIngredientList() != null &&
                mRecipe.getIngredientList().size() != 0) {
            /* use just 1 position instead of list size */
            ingredients_endPos += 1;
        }
        steps_startPos = ingredients_endPos;
        steps_endPos = steps_startPos;
        if(mRecipe.getStepList() != null) {
            steps_endPos += mRecipe.getStepList().size() + 1;
        }

        Timber.d("getItemViewType - POSITION: %d", position);
        Timber.d("getItemViewType - serving: [%d, %d[", serving_startPos, serving_endPos);
        Timber.d("getItemViewType - ingred : [%d, %d[", ingredients_startPos, ingredients_endPos);
        Timber.d("getItemViewType - step   : [%d, %d[", steps_startPos, steps_endPos);

        if(position == 0) {
            Timber.d("getItemViewType pos: %d - ITEMVIEWTYPE_RECIPE_SERVINGS", position);
            viewType = ITEMVIEWTYPE_RECIPE_SERVINGS;
        }
        else if((ingredients_startPos <= position) && (position < ingredients_endPos)) {
            Timber.d("getItemViewType pos: %d - ITEMVIEWTYPE_RECIPE_INGREDIENT", position);
            viewType = ITEMVIEWTYPE_RECIPE_INGREDIENT;
        }
        else if((steps_startPos <= position) && (position < steps_endPos)) {
            Timber.d("getItemViewType pos: %d - ITEMVIEWTYPE_RECIPE_STEP", position);
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
