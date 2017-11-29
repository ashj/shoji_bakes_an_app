package com.example.shoji.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.shoji.bakingapp.tests.MainActivityTestUtils;
import com.example.shoji.bakingapp.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityBasicTest_sw600dp {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule(MainActivity.class);

    private IdlingResource mIdlingResource;

    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        //Espresso.registerIdlingResources(mIdlingResource);
        IdlingRegistry.getInstance().register(mIdlingResource);
    }


    @Test
    public void testIdlingResourceAndOneIngredient() {
        int ingredientResId = R.id.fragment_recipe_ingredient_list_recycler_view;

        MainActivityTestUtils.testIdlingResourceAndOneIngredient(ingredientResId);
    }


    @Test
    public void testIdlingResourceAndOneStep() {
        MainActivityTestUtils.testIdlingResourceAndOneStep();
    }

    @Test
    public void testIdlingResourceAndOneStepBackwards() {
        MainActivityTestUtils.testIdlingResourceAndOneStepBackwards();
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            //Espresso.unregisterIdlingResources(mIdlingResource);
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}
