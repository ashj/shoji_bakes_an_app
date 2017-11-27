package com.example.shoji.bakingapp.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.shoji.bakingapp.pojo.Recipe;
import com.example.shoji.bakingapp.pojo.RecipeIngredient;
import com.example.shoji.bakingapp.pojo.RecipeStep;
import com.example.shoji.bakingapp.provider.RecipeContract;
import com.example.shoji.bakingapp.provider.RecipeIngredientContract;
import com.example.shoji.bakingapp.provider.RecipeIngredientProvider;
import com.example.shoji.bakingapp.provider.RecipeProvider;
import com.example.shoji.bakingapp.provider.RecipeStepContract;
import com.example.shoji.bakingapp.provider.RecipeStepProvider;

import java.util.ArrayList;

import timber.log.Timber;


public class RecipeProviderUtils {
    public static void insertRecipesToDb(Context context, ArrayList<Recipe> recipes) {
        if(recipes == null) {
            Timber.w("insertRecipesToDb -- recipes=(null)");
            return;
        }

        for(Recipe recipe : recipes)
            insertRecipeToDb(context, recipe);
    }

    public static void insertRecipeToDb(Context context, Recipe recipe) {
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues newRecipe = new ContentValues();
        newRecipe.put(RecipeContract.COLUMN_RECIPE_ID, recipe.getId());
        newRecipe.put(RecipeContract.COLUMN_NAME, recipe.getName());
        newRecipe.put(RecipeContract.COLUMN_SERVINGS, recipe.getServings());
        newRecipe.put(RecipeContract.COLUMN_IMAGE, recipe.getImage());

        Uri uri = contentResolver.insert(RecipeProvider.Recipes.CONTENT_URI, newRecipe);
        String newId = uri.getLastPathSegment();
        Timber.d("ADDED RECIPE to DB (uri:%s) - %s - %s", newId, recipe.getName(), uri);

        insertRecipeIngreditentsToDb(contentResolver, recipe, newId);
        insertRecipeStepsToDb(contentResolver, recipe, newId);
    }

    public static void insertRecipeIngreditentsToDb(ContentResolver resolver, Recipe recipe, String recipeUri) {
        ArrayList<RecipeIngredient> ingredients = recipe.getIngredientList();
        if(ingredients == null) {
            Timber.w("insertRecipesToDb -- recipes=(null)");
            return;
        }

        for(RecipeIngredient ingredient : ingredients)
            insertRecipeIngreditentToDb(resolver, ingredient,recipeUri);
    }

    public static void insertRecipeIngreditentToDb(ContentResolver resolver, RecipeIngredient ingredient, String recipeUri) {
        ContentValues cv = new ContentValues();

        cv.put(RecipeIngredientContract.COLUMN_RECIPE_ID, recipeUri);
        cv.put(RecipeIngredientContract.COLUMN_QUANTITY, ingredient.getQuantity());
        cv.put(RecipeIngredientContract.COLUMN_MEASURE, ingredient.getMeasure());
        cv.put(RecipeIngredientContract.COLUMN_DESCRIPTION, ingredient.getDescription());

        Uri uri = resolver.insert(RecipeIngredientProvider.Ingredients.CONTENT_URI, cv);
        String newId = uri.getLastPathSegment();
        Timber.d("ADDED INGREDIENT to DB (uri:%s - %s) - %s", newId, recipeUri, ingredient.getDescription());

    }


    public static void insertRecipeStepsToDb(ContentResolver resolver, Recipe recipe, String recipeUri) {
        ArrayList<RecipeStep> steps = recipe.getStepList();
        if(steps == null) {
            Timber.w("insertRecipesToDb -- recipes=(null)");
            return;
        }

        for(RecipeStep step : steps)
            insertRecipeStepToDb(resolver, step, recipeUri);
    }

    public static void insertRecipeStepToDb(ContentResolver resolver, RecipeStep step, String recipeUri) {
        ContentValues cv = new ContentValues();

        cv.put(RecipeStepContract.COLUMN_RECIPE_ID, recipeUri);
        cv.put(RecipeStepContract.COLUMN_STEP_ID, step.getId());
        cv.put(RecipeStepContract.COLUMN_SHORT_DESCRIPTION, step.getShortDescription());
        cv.put(RecipeStepContract.COLUMN_LONG_DESCRIPTION, step.getLongDescription());
        cv.put(RecipeStepContract.COLUMN_VIDEO_URL, step.getVideoUrl());
        cv.put(RecipeStepContract.COLUMN_THUMBNAIL_URL, step.getThumbnailUrl());

        Uri uri = resolver.insert(RecipeStepProvider.Steps.CONTENT_URI, cv);

        String newId = uri.getLastPathSegment();
        Timber.d("ADDED STEP to DB (uri:%s - %s) - %s. %s", newId, recipeUri, step.getId(), step.getShortDescription());

    }

    //TEST
    public static Recipe getRecipeFromDb(Context context, String recipeUriId) {
        Recipe recipe = null;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(RecipeProvider.Recipes.CONTENT_URI,
                null,
                RecipeContract._ID + "=" + recipeUriId,
                null, null);

        if(cursor != null && cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(RecipeContract.COLUMN_NAME));
            String id = cursor.getString(cursor.getColumnIndex(RecipeContract.COLUMN_RECIPE_ID));
            String servings = cursor.getString(cursor.getColumnIndex(RecipeContract.COLUMN_SERVINGS));
            String image = cursor.getString(cursor.getColumnIndex(RecipeContract.COLUMN_IMAGE));

            recipe = new Recipe();
            recipe.setId(id);
            recipe.setName(name);
            recipe.setServings(servings);
            recipe.setImage(image);

            recipe.setIngredientList(getIngredientsFromDb(context, recipeUriId));
            recipe.setStepList(getStepsFromDb(context, recipeUriId));
            Timber.d("Queried: %s", recipe.toString());
        }
        cursor.close();
        return recipe;
    }

    public static ArrayList<RecipeIngredient> getIngredientsFromDb(Context context, String recipeUriId) {
        ArrayList<RecipeIngredient> ingredients = null;

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(RecipeIngredientProvider.Ingredients.CONTENT_URI,
                null,
                RecipeIngredientContract.COLUMN_RECIPE_ID + "=" + recipeUriId,
                null, null);

        if(cursor != null) {
            ingredients = new ArrayList<>();
            while(cursor.moveToNext()) {
                RecipeIngredient ingredient = new RecipeIngredient();

                String quantity =
                        cursor.getString(cursor.getColumnIndex(RecipeIngredientContract.COLUMN_QUANTITY));
                String measure =
                        cursor.getString(cursor.getColumnIndex(RecipeIngredientContract.COLUMN_MEASURE));
                String description =
                        cursor.getString(cursor.getColumnIndex(RecipeIngredientContract.COLUMN_DESCRIPTION));

                ingredient.setQuantity(quantity);
                ingredient.setMeasure(measure);
                ingredient.setDescription(description);

                ingredients.add(ingredient);
            }

        }

        return ingredients;
    }


    public static ArrayList<RecipeStep> getStepsFromDb(Context context, String recipeUriId) {
        ArrayList<RecipeStep> steps = null;

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(RecipeStepProvider.Steps.CONTENT_URI,
                null,
                RecipeStepContract.COLUMN_RECIPE_ID + "=" + recipeUriId,
                null, null);

        if (cursor != null) {
            steps = new ArrayList<>();
            while (cursor.moveToNext()) {
                RecipeStep step = new RecipeStep();

                String id =
                        cursor.getString(cursor.getColumnIndex(RecipeStepContract.COLUMN_STEP_ID));
                String shortDescription =
                        cursor.getString(cursor.getColumnIndex(RecipeStepContract.COLUMN_SHORT_DESCRIPTION));
                String longDescription =
                        cursor.getString(cursor.getColumnIndex(RecipeStepContract.COLUMN_LONG_DESCRIPTION));
                String videoUrl =
                        cursor.getString(cursor.getColumnIndex(RecipeStepContract.COLUMN_VIDEO_URL));
                String thumbnailUrl =
                        cursor.getString(cursor.getColumnIndex(RecipeStepContract.COLUMN_THUMBNAIL_URL));

                step.setId(id);
                step.setShortDescription(shortDescription);
                step.setLongDescription(longDescription);
                step.setVideoUrl(videoUrl);
                step.setThumbnailUrl(thumbnailUrl);

                steps.add(step);
            }

        }

        return steps;
    }
}
