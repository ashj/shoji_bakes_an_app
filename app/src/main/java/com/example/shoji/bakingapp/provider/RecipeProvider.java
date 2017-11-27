package com.example.shoji.bakingapp.provider;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(
        authority = RecipeProvider.AUTHORITY,
        database = RecipeDatabase.class)
public class RecipeProvider {
    public static final String AUTHORITY = "com.example.shoji.bakingapp.provider.recipes";

    @TableEndpoint(table = RecipeDatabase.TABLE_NAME)
    public static class Recipes {
        public static final String PATH = RecipeDatabase.TABLE_NAME;
        @ContentUri(
                path = PATH,
                type = "vnd.android.cursor.dir/" + PATH,
                defaultSort = RecipeContract._ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    }
}
