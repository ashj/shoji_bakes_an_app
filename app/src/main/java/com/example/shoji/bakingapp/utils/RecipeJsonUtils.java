package com.example.shoji.bakingapp.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static void listRecipes(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            Timber.d("jsonArray length: %d", jsonArray.length());
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject recipe = jsonArray.getJSONObject(i);

                String id = getString(recipe, JSON_RECIPE_ID);
                String name = getString(recipe, JSON_RECIPE_NAME);
                Timber.d("recipe id: %s / recipe name: %s", id, name);

                //
                JSONArray ingredients = recipe.getJSONArray(JSON_RECIPE_INGREDIENTS);
                listIngreditents(ingredients);

                //
                JSONArray steps = recipe.getJSONArray(JSON_RECIPE_STEPS);
                listSteps(steps);

                Timber.d("\n");
            }


        } catch (JSONException e) {
            Timber.e(e.getMessage());
        }


    }

    public static void listIngreditents(JSONArray ingredients) {
        try {
            for (int i = 0; i < ingredients.length(); ++i) {
                JSONObject ingredient = ingredients.getJSONObject(i);

                String description = getString(ingredient, JSON_INGREDIENT_DESCRIPTION);
                String measure = getString(ingredient, JSON_INGREDIENT_MEASURE);
                String quantity = getString(ingredient, JSON_INGREDIENT_QUANTITY);
                Timber.d("ingredient(%d): %s ; measure: %s ; quantity: %s",
                        i, description, measure, quantity);


            }
        } catch (JSONException e) {
            Timber.e(e.getMessage());
        }

    }

    public static void listSteps(JSONArray steps) {
        try {
            for (int i = 0; i < steps.length(); ++i) {
                JSONObject step = steps.getJSONObject(i);



                String id = getString(step, JSON_STEPS_ID);
                String short_description = getString(step, JSON_STEPS_SHORT_DESCRIPTION);
                String long_description = getString(step, JSON_STEPS_LONG_DESCRIPTION);
                String video_url = getString(step, JSON_STEPS_VIDEO_URL);
                String thumbnail_url = getString(step, JSON_STEPS_THUMBNAIL_URL);

                Timber.d("step((%s))(%d): short_description: %s",
                        id, i, short_description);
                Timber.d("step((%s))(%d): long_description: %s",
                        id, i, long_description);
                Timber.d("step((%s))(%d): video_url: %s",
                        id, i, video_url);
                Timber.d("step((%s))(%d): thumbnail_url: %s",
                        id, i, thumbnail_url);

            }
        } catch (JSONException e) {
            Timber.e(e.getMessage());
        }

    }


}
