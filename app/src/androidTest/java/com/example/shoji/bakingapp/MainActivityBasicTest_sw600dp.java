package com.example.shoji.bakingapp;

import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.shoji.bakingapp.tests.MainActivityTestUtils;
import com.example.shoji.bakingapp.ui.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.core.internal.deps.dagger.internal.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

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
        String text = "2 CUP Graham Cracker crumbs";
        int recipePosition = 0;
        int ingredientPosition = 1;
        int ingredientListPosition = 0;

        // Main activity, click on first
        onView(withId(R.id.activity_main_recipes_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(recipePosition, click()));

        onView(withId(R.id.activity_recipe_master_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(ingredientPosition, click()));


        onView(withId(R.id.fragment_recipe_ingredient_list_recycler_view))
                .perform(scrollToPosition(ingredientListPosition))
                .check(matches(atPosition(ingredientListPosition, withText(text))));

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

    //https://stackoverflow.com/questions/31394569/how-to-assert-inside-a-recyclerview-in-espresso
    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
