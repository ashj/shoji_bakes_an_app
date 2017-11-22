package com.example.shoji.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.data.RecipeMasterListAdapter;
import com.example.shoji.bakingapp.pojo.Recipe;

import timber.log.Timber;

public class RecipeActivity extends AppCompatActivity implements RecipeMasterListAdapter.OnClickListener {
    public static final String EXTRA_RECIPE_DATA = "extra-recipe-data";

    private boolean mIsTabletMode;

    private Context mContext;
    private Recipe mRecipe;
    private FrameLayout mContentBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        mContext = this;

        mIsTabletMode = isTabletMode();
        Timber.d("mIsTabletMode? : %b", mIsTabletMode);

        mRecipe = getRecipeFromIntent();
    }

    private boolean isTabletMode() {
        if((mContentBody = findViewById(R.id.activity_recipe_content_body))
                == null)
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
        Intent intent = new Intent(this, RecipeIngredientActivity.class);
        intent.putExtra(EXTRA_RECIPE_DATA, mRecipe);
        startActivity(intent);
        Timber.d("Process onClickIngredient");
    }

    @Override
    public void onClickStep(int position) {
        Timber.d("Process onClickStep at pos: %d", position);

    }
}
