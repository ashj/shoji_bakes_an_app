package com.example.shoji.bakingapp;

import android.content.ClipData;
import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.shoji.bakingapp.ui.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.core.internal.deps.dagger.internal.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
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

        onView(withId(R.id.activity_recipe_ingredients))
                .perform(scrollToPosition(ingredientListPosition))
                .check(matches(atPosition(ingredientListPosition, withText(text))));

    }

    @Test
    public void testIdlingResourceAndOneStep() {
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

    @Test
    public void testIdlingResourceAndOneStepBackwards() {
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
