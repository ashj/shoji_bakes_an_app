package com.example.shoji.bakingapp.utils;

import android.content.Context;

import com.example.shoji.bakingapp.pojo.Recipe;
import com.example.shoji.bakingapp.pojo.RecipeIngredient;
import com.example.shoji.bakingapp.pojo.RecipeStep;

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

    public static void listRecipes(Context context, String jsonString) {
        try {
            String newRecipeUri = null;
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

                newRecipeUri = RecipeProviderUtils.insertRecipeToDb(context, recipe);

                // Get ingredients list
                if(recipeJsonObject.has(JSON_RECIPE_INGREDIENTS)) {
                    JSONArray ingredients = recipeJsonObject.getJSONArray(JSON_RECIPE_INGREDIENTS);
                    listIngredients(context, ingredients, newRecipeUri);
                }


                // Get steps list
                if(recipeJsonObject.has(JSON_RECIPE_STEPS)) {
                    JSONArray steps = recipeJsonObject.getJSONArray(JSON_RECIPE_STEPS);
                    listSteps(context, steps, newRecipeUri);
                }

            }


        } catch (JSONException e) {
            Timber.e(e.getMessage());
        }
    }


    public static void listIngredients(Context context, JSONArray ingredients, String recipeUriId) {
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

                RecipeProviderUtils.insertRecipeIngreditentToDb(
                        context.getContentResolver(),
                        recipeIngredient,
                        recipeUriId);
            }
        } catch (JSONException e) {
            Timber.e(e.getMessage());
        }
    }


    public static void listSteps(Context context, JSONArray steps, String recipeUriId) {
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

                RecipeProviderUtils.insertRecipeStepToDb(context.getContentResolver(),
                        recipeStep, recipeUriId);
            }
        } catch (JSONException e) {
            Timber.e(e.getMessage());
        }
    }




}
