package com.example.shoji.bakingapp.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

public class RecipeJsonUtils {

    private static final String JSON_RECIPE_ID = "id";
    private static final String JSON_RECIPE_NAME = "name";
    private static final String JSON_RECIPE_INGREDIENTS = "ingredients";
    private static final String JSON_RECIPE_SERVINGS = "servings";
    private static final String JSON_RECIPE_IMAGE = "image";


    private static final String JSON_INGREDIENT_QUANTITY = "quantity";
    private static final String JSON_INGREDIENT_MEASURE = "measure";
    private static final String JSON_INGREDIENT_DESCRIPTION = "ingredient";

    private static final String JSON_RECIPE_STEPS = "steps";
    private static final String JSON_STEPS_ID = "id";
    private static final String JSON_STEPS_SHORT_DESCRIPTION = "shortDescription";
    private static final String JSON_STEPS_LONG_DESCRIPTION = "description";
    private static final String JSON_STEPS_VIDEO_URL = "videoURL";
    private static final String JSON_STEPS_THUMBNAIL_URL = "thumbnailURL";

    private static String getString(JSONObject jsonObject, String name) throws JSONException {
        if(jsonObject.has(name))
            return jsonObject.getString(name);
        Timber.w("getString - jsonObject does not contain name: %s", name);
        return "";
    }

    public static ArrayList<Recipe> listRecipes(String jsonString) {
        ArrayList<Recipe> recipeList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            Timber.d("jsonArray length: %d", jsonArray.length());
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject recipeJsonObject = jsonArray.getJSONObject(i);

                Recipe recipe = new Recipe();

                String id = getString(recipeJsonObject, JSON_RECIPE_ID);
                String name = getString(recipeJsonObject, JSON_RECIPE_NAME);
                String servings = getString(recipeJsonObject, JSON_RECIPE_SERVINGS);
                String image = getString(recipeJsonObject, JSON_RECIPE_IMAGE);
//                Timber.d("recipe id: %s / recipe name: %s", id, name);

                recipe.setId(id);
                recipe.setName(name);
                recipe.setServings(servings);
                recipe.setImage(image);

                // Get ingredients list
                ArrayList<RecipeIngredient> ingredientList = null;
                if(recipeJsonObject.has(JSON_RECIPE_INGREDIENTS)) {
                    JSONArray ingredients = recipeJsonObject.getJSONArray(JSON_RECIPE_INGREDIENTS);
                    ingredientList = listIngredients(ingredients);
                }
                recipe.setIngredientList(ingredientList);

                // Get steps list
                ArrayList<RecipeStep> stepList = null;
                if(recipeJsonObject.has(JSON_RECIPE_STEPS)) {
                    JSONArray steps = recipeJsonObject.getJSONArray(JSON_RECIPE_STEPS);
                    stepList = listSteps(steps);
                }
                recipe.setStepList(stepList);

                recipeList.add(recipe);

            }


        } catch (JSONException e) {
            Timber.e(e.getMessage());
            return null;
        }
        return recipeList;
    }


    public static ArrayList<RecipeIngredient> listIngredients(JSONArray ingredients) {
        ArrayList<RecipeIngredient> ingredientList = new ArrayList<>();
        try {
            for (int i = 0; i < ingredients.length(); ++i) {
                JSONObject ingredient = ingredients.getJSONObject(i);

                RecipeIngredient recipeIngredient = new RecipeIngredient();

                String description = getString(ingredient, JSON_INGREDIENT_DESCRIPTION);
                String measure = getString(ingredient, JSON_INGREDIENT_MEASURE);
                String quantity = getString(ingredient, JSON_INGREDIENT_QUANTITY);

                recipeIngredient.setDescription(description);
                recipeIngredient.setMeasure(measure);
                recipeIngredient.setQuantity(quantity);

                ingredientList.add(recipeIngredient);

//                Timber.d("ingredient(%d): %s ; measure: %s ; quantity: %s",
//                        i, description, measure, quantity);
            }
        } catch (JSONException e) {
            Timber.e(e.getMessage());
            return null;
        }
        return ingredientList;
    }


    public static ArrayList<RecipeStep> listSteps(JSONArray steps) {
        ArrayList<RecipeStep> stepList = new ArrayList<>();
        try {
            for (int i = 0; i < steps.length(); ++i) {
                JSONObject step = steps.getJSONObject(i);

                RecipeStep recipeStep = new RecipeStep();

                String id = getString(step, JSON_STEPS_ID);
                String short_description = getString(step, JSON_STEPS_SHORT_DESCRIPTION);
                String long_description = getString(step, JSON_STEPS_LONG_DESCRIPTION);
                String video_url = getString(step, JSON_STEPS_VIDEO_URL);
                String thumbnail_url = getString(step, JSON_STEPS_THUMBNAIL_URL);

                recipeStep.setId(id);
                recipeStep.setShortDescription(short_description);
                recipeStep.setLongDescription(long_description);
                recipeStep.setThumbnailUrl(thumbnail_url);
                recipeStep.setVideoUrl(video_url);

//                Timber.d("step((%s))(%d): short_description: %s",
//                        id, i, short_description);
//                Timber.d("step((%s))(%d): long_description: %s",
//                        id, i, long_description);
//                Timber.d("step((%s))(%d): video_url: %s",
//                        id, i, video_url);
//                Timber.d("step((%s))(%d): thumbnail_url: %s",
//                        id, i, thumbnail_url);

                stepList.add(recipeStep);
            }
        } catch (JSONException e) {
            Timber.e(e.getMessage());
            return null;
        }

        return stepList;
    }


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
}
