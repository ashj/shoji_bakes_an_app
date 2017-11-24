package com.example.shoji.bakingapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.shoji.bakingapp.R;

import timber.log.Timber;

public class RecipeStepActivity
        extends AppCompatActivity
        implements RecipeStepFragment.OnClickNavButtonListener {
    public static final String EXTRA_STEP_NUMBER = "extra-step-number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);
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

    @Override
    public void onClickPrev(int currentPosition) {
        Timber.d("Tapped prev!");
    }

    @Override
    public void onClickNext(int currentPosition) {
        Timber.d("Tapped next!");
    }
}
