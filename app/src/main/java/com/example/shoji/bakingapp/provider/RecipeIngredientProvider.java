package com.example.shoji.bakingapp.provider;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(
        authority = RecipeIngredientProvider.AUTHORITY,
        database = RecipeIngredientDatabase.class)
public class RecipeIngredientProvider {
    public static final String AUTHORITY = "com.example.shoji.bakingapp.provider.ingredients";

    @TableEndpoint(table = RecipeIngredientDatabase.TABLE_NAME)
    public static class Ingredients {
        public static final String PATH = RecipeIngredientDatabase.TABLE_NAME;
        @ContentUri(
                path = PATH,
                type = "vnd.android.cursor.dir/" + PATH,
                defaultSort = RecipeIngredientContract._ID + " DESC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    }
}
