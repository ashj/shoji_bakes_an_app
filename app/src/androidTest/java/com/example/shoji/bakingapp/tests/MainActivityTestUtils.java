package com.example.shoji.bakingapp.tests;


import android.support.test.espresso.contrib.RecyclerViewActions;

import com.example.shoji.bakingapp.R;

import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class MainActivityTestUtils {
    public static void testIdlingResourceAndOneStep() {
        String text1 = "Recipe Introduction";
        String text2 = "9. Cut and serve.";
        int recipePosition = 1;
        int stepPosition = 2;


        // Main activity, click on first
        onView(withId(R.id.activity_main_recipes_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(recipePosition, click()));

        onView(withId(R.id.activity_recipe_master_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(stepPosition, click()));

        onView(withId(R.id.fragment_recipe_step_long_description))
                .check(matches(withText(text1)));

        for(int i=0; i < 9; i++)
            onView(withId(R.id.navbutton_recipe_next_button))
                    .perform(click());

        onView(withId(R.id.fragment_recipe_step_long_description))
                .check(matches(withText(text2)));
    }

    public static void testIdlingResourceAndOneStepBackwards() {
        String text1 = "Recipe Introduction";
        String text2 = "9. Cut and serve.";
        int recipePosition = 1;
        int stepPosition = 11;


        // Main activity, click on first
        onView(withId(R.id.activity_main_recipes_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(recipePosition, click()));

        onView(withId(R.id.activity_recipe_master_list))
                .perform(scrollToPosition(stepPosition))
                .perform(RecyclerViewActions.actionOnItemAtPosition(stepPosition, click()));

        onView(withId(R.id.fragment_recipe_step_long_description))
                .check(matches(withText(text2)));

        for(int i=0; i < 9; i++)
            onView(withId(R.id.navbutton_recipe_previous_button))
                    .perform(click());

        onView(withId(R.id.fragment_recipe_step_long_description))
                .check(matches(withText(text1)));
    }
}
