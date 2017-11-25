package com.example.shoji.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.data.RecipeMasterListAdapter;
import com.example.shoji.bakingapp.pojo.Recipe;

import timber.log.Timber;

public class RecipeActivity extends AppCompatActivity
        implements RecipeMasterListAdapter.OnClickListener,
        RecipeStepFragment.OnClickNavButtonListener {
    public static final String EXTRA_RECIPE_DATA = "extra-recipe-data";

    private boolean mIsTabletMode;
    private RecipeStepFragmentManager mStepFragmentManager;

    private Context mContext;
    private Recipe mRecipe;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        mContext = this;

        mIsTabletMode = isTabletMode();
        Timber.d("mIsTabletMode? : %b", mIsTabletMode);

        if(mIsTabletMode) {
            mStepFragmentManager = new RecipeStepFragmentManager(getSupportFragmentManager(), R.id.activity_recipe_content_body);
        }

        mRecipe = getRecipeFromIntent();
    }

    private boolean isTabletMode() {
        if((findViewById(R.id.activity_recipe_content_body)) == null)
            return false;
        return true;
    }

    private Recipe getRecipeFromIntent() {
        Intent intent = getIntent();
        if(intent == null)
            return null;

        if(!intent.hasExtra(RecipeActivity.EXTRA_RECIPE_DATA))
            return null;

        return intent.getParcelableExtra(RecipeActivity.EXTRA_RECIPE_DATA);
    }

    @Override
    public void onClickIngredient() {
        Timber.d("Process onClickIngredient");
        if(mIsTabletMode) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            RecipeIngredientFragment ingredientFragment = new RecipeIngredientFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_recipe_content_body, ingredientFragment)
                    .commit();

        } else {
            Intent intent = new Intent(this, RecipeIngredientActivity.class);
            intent.putExtra(EXTRA_RECIPE_DATA, mRecipe);

            startActivity(intent);
        }

    }

    @Override
    public void onClickStep(int position) {
        if(mIsTabletMode) {
            mStepFragmentManager.replaceStepFragment(position);
        } else {
            Bundle args  = new Bundle();
            args.putInt(RecipeStepActivity.EXTRA_STEP_NUMBER, position);

            Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtra(EXTRA_RECIPE_DATA, mRecipe);
            intent.putExtra(RecipeStepActivity.EXTRA_STEP_NUMBER, args);
            startActivity(intent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* For tablet mode */
    @Override
    public void onClickPrev(int currentPosition) {
        mStepFragmentManager.replaceStepFragment(currentPosition - 1);
    }

    @Override
    public void onClickNext(int currentPosition) {
        mStepFragmentManager.replaceStepFragment(currentPosition + 1);
    }
}
