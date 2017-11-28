package com.example.shoji.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.shoji.bakingapp.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class MainActivityBasicTest {
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
    public void idlingResourceTest() {
        String text = "2 CUP Graham Cracker crumbs";
        int recipePosition = 0;
        int ingredientPosition = 1;
        int ingredientListPosition = 0;

        // Main activity, click on first
        onView(withId(R.id.activity_main_recipes_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(recipePosition, click()));

        onView(withId(R.id.activity_recipe_master_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(ingredientPosition, click()));

        onView(withId(R.id.activity_recipe_ingredients))
                .perform(RecyclerViewActions.actionOnItemAtPosition(ingredientListPosition, click()));
               //check(matches(withText(text))));


    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            //Espresso.unregisterIdlingResources(mIdlingResource);
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

}
