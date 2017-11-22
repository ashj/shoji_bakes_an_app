package com.example.shoji.bakingapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.shoji.bakingapp.R;

import timber.log.Timber;

public class RecipeActivity extends AppCompatActivity {
    public static final String EXTRA_RECIPE_DATA = "extra-recipe-data";
    private boolean mIsTabletMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        mIsTabletMode = isTabletMode();

        Timber.d("mIsTabletMode? : %b", mIsTabletMode);
    }

    private boolean isTabletMode() {
        if(findViewById(R.id.activity_recipe_content_body) == null)
            return false;
        return true;
    }
}
