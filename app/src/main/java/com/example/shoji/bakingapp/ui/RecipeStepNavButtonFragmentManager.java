package com.example.shoji.bakingapp.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class RecipeStepNavButtonFragmentManager {
    private FragmentManager mFragmentManager;
    private int mResFrameLayoutId;

    public RecipeStepNavButtonFragmentManager(FragmentManager fragmentManager, int resFrameLayoutId) {
        mFragmentManager = fragmentManager;
        mResFrameLayoutId = resFrameLayoutId;
    }

    public RecipeStepFragment prepareStepFragment(int position) {
        Bundle args  = new Bundle();
        args.putInt(RecipeStepActivity.EXTRA_STEP_NUMBER, position);
        RecipeStepFragment stepFragment = new RecipeStepFragment();
        stepFragment.setArguments(args);

        return stepFragment;
    }


    public void replaceStepFragment(int position) {
        RecipeStepFragment stepFragment = prepareStepFragment(position);

        mFragmentManager.beginTransaction()
                .replace(mResFrameLayoutId, stepFragment)
                .commit();
    }


}
