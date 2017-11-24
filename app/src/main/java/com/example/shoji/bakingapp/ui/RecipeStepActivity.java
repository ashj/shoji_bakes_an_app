package com.example.shoji.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.shoji.bakingapp.R;

import timber.log.Timber;

public class RecipeStepActivity
        extends AppCompatActivity
        implements RecipeStepFragment.OnClickNavButtonListener {
    public static final String EXTRA_STEP_NUMBER = "extra-step-number";
    private static final int INITIAL_STEP_POSITION = 0;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_step);


        int position = getPositionFromIntent();
        RecipeStepFragment stepFragment = prepareStepFragment(position);
        Timber.d("GOT POSITION: %d", position);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .add(R.id.activity_recipe_step_content_body, stepFragment)
                .commit();
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
        Timber.d("currentPosition = %d, Tapped prev!", currentPosition);
        replaceStepFragment(currentPosition - 1);
    }

    @Override
    public void onClickNext(int currentPosition) {
        Timber.d("currentPosition = %d, Tapped next!", currentPosition);
        replaceStepFragment(currentPosition + 1);
    }

    private int getPositionFromIntent() {
        Intent intent = getIntent();
        int result = INITIAL_STEP_POSITION;
        if(intent != null) {
            Bundle args = intent.getBundleExtra(EXTRA_STEP_NUMBER);
            if(args != null) {
                result = args.getInt(EXTRA_STEP_NUMBER);
            }

        }
        return result;
    }

    private RecipeStepFragment prepareStepFragment(int position) {
        Bundle args  = new Bundle();
        args.putInt(RecipeStepActivity.EXTRA_STEP_NUMBER, position);
        RecipeStepFragment stepFragment = new RecipeStepFragment();
        stepFragment.setArguments(args);

        return stepFragment;
    }

    private void replaceStepFragment(int position) {
        RecipeStepFragment stepFragment = prepareStepFragment(position);

        mFragmentManager.beginTransaction()
                .add(R.id.activity_recipe_step_content_body, stepFragment)
                .commit();
    }
}
