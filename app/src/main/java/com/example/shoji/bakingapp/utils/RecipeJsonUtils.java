package com.example.shoji.bakingapp.utils;

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
//                Timber.d("recipe id: %s / recipe name: %s", id, name);

                recipe.setId(id);
                recipe.setName(name);

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


}
