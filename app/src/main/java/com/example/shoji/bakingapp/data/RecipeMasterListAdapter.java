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
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SimpleViewHolder.OnClickListener
{
    private static final int ITEMVIEWTYPE_RECIPE_INGREDIENT = 0;
    private static final int ITEMVIEWTYPE_RECIPE_STEP = 1;
    private static final int ITEMVIEWTYPE_RECIPE_SERVINGS = 2;
    private static final int ITEMVIEWTYPE_RECIPE_ILLUSTRATION_IMAGE = 3;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.d("onCreateViewHolder");
        int layoutResId;
        RecyclerView.ViewHolder viewHolder = null;

        int textViewResId = R.id.recipe_adapter_title_tv;
        switch (viewType) {
            case ITEMVIEWTYPE_RECIPE_ILLUSTRATION_IMAGE:
                viewHolder = createImageHolder(parent);
                break;
            case ITEMVIEWTYPE_RECIPE_SERVINGS:
                layoutResId = R.layout.recipe_serving_adapter_item;
                viewHolder = createSimpleViewHolder(parent, layoutResId, textViewResId);
                break;
            case ITEMVIEWTYPE_RECIPE_INGREDIENT:
            case ITEMVIEWTYPE_RECIPE_STEP:
                layoutResId = R.layout.recipe_details_adapter_item;
                viewHolder = createSimpleViewHolder(parent, layoutResId, textViewResId);
                break;
                default:
                    break;
        }

        return viewHolder;
    }

    private SimpleImageViewHolder createImageHolder(ViewGroup parent) {
        boolean attachToRoot = false;
        int layoutResId = R.layout.recipe_illustration_adapter_item;
        int imageViewResId = R.id.recipe_adapter_illustration_image;

        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(layoutResId, parent, attachToRoot);

        SimpleImageViewHolder viewHolder = new SimpleImageViewHolder(itemView, imageViewResId);
        return viewHolder;
    }

    private SimpleViewHolder createSimpleViewHolder(ViewGroup parent, int layoutResId, int testViewResId) {
        boolean attachToRoot = false;
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(layoutResId, parent, attachToRoot);
        SimpleViewHolder.OnClickListener onClickHandler = this;
        SimpleViewHolder viewHolder = new SimpleViewHolder(itemView, testViewResId, onClickHandler);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        String text;
        SimpleImageViewHolder imageViewHolder;
        SimpleViewHolder textViewHolder;
        switch(viewType) {
            case ITEMVIEWTYPE_RECIPE_ILLUSTRATION_IMAGE:
                Timber.d("onBindViewHolder, pos %d, ITEMVIEWTYPE_RECIPE_ILLUSTRATION_IMAGE", position);
                String url = mRecipe.getImage();
                imageViewHolder = (SimpleImageViewHolder) holder;
                imageViewHolder.bindViewHolder(mContext, url);
                break;

            case ITEMVIEWTYPE_RECIPE_SERVINGS:
                text = mContext.getString(R.string.recipe_servings,mRecipe.getServings());
                Timber.d("onBindViewHolder, pos %d, ITEMVIEWTYPE_RECIPE_SERVINGS", position);
                textViewHolder = (SimpleViewHolder) holder;
                textViewHolder.bindViewHolder(text);
                break;

            case ITEMVIEWTYPE_RECIPE_INGREDIENT:
                Timber.d("onBindViewHolder, pos %d, ITEMVIEWTYPE_RECIPE_INGREDIENT", position);
                textViewHolder = (SimpleViewHolder) holder;
                textViewHolder.bindViewHolder(mContext.getString(R.string.ingredients_list));
                break;
            case ITEMVIEWTYPE_RECIPE_STEP:
                int positionOffset = getPositionOffset(position);
                Timber.d("onBindViewHolder, pos %d/ offset: %d, ITEMVIEWTYPE_RECIPE_STEP",
                        position, positionOffset);

                textViewHolder = (SimpleViewHolder) holder;
                textViewHolder.bindViewHolder(mRecipe.getStepList().get(positionOffset).getShortDescription());
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
            if(mRecipe.getImage().length() != 0) {
            /* use just 1 position */
                totalCount += 1;
            }
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

        int image_startPos;
        int image_endPos;

        int serving_startPos;
        int serving_endPos;

        int ingredients_startPos;
        int ingredients_endPos;

        int steps_startPos;
        int steps_endPos;

        // image view boundaries
        if(mRecipe.getImage().length() != 0) {
            image_startPos = 0;
            image_endPos = 1;
        }
        else {
            image_startPos = -1;
            image_endPos = 0;
        }

        // serving view boundaries
        serving_startPos = image_endPos;
        serving_endPos = serving_startPos + 1;

        // ingredients view boundaries
        ingredients_startPos = serving_endPos;
        ingredients_endPos = ingredients_startPos;
        if(mRecipe.getIngredientList() != null &&
                mRecipe.getIngredientList().size() != 0) {
            /* use just 1 position instead of list size */
            ingredients_endPos += 1;
        }
        // steps view boundaries
        steps_startPos = ingredients_endPos;
        steps_endPos = steps_startPos;
        if(mRecipe.getStepList() != null) {
            steps_endPos += mRecipe.getStepList().size() + 1;
        }

        Timber.d("getItemViewType - POSITION: %d", position);
        Timber.d("getItemViewType - image  : [%d, %d[", image_startPos, image_endPos);
        Timber.d("getItemViewType - serving: [%d, %d[", serving_startPos, serving_endPos);
        Timber.d("getItemViewType - ingred.: [%d, %d[", ingredients_startPos, ingredients_endPos);
        Timber.d("getItemViewType - step   : [%d, %d[", steps_startPos, steps_endPos);

        if((image_startPos <= position) && (position < image_endPos)) {
            Timber.d("getItemViewType pos: %d - ITEMVIEWTYPE_RECIPE_ILLUSTRATION_IMAGE", position);
            viewType = ITEMVIEWTYPE_RECIPE_ILLUSTRATION_IMAGE;
        }
        else if((serving_startPos <= position) && (position < serving_endPos)) {
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
