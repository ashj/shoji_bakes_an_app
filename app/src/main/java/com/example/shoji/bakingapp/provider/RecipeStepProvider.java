package com.example.shoji.bakingapp.provider;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(
        authority = RecipeStepProvider.AUTHORITY,
        database = RecipeStepDatabase.class)
public class RecipeStepProvider {
    public static final String AUTHORITY = "com.example.shoji.bakingapp.provider.steps";

    @TableEndpoint(table = RecipeStepDatabase.TABLE_NAME)
    public static class Steps {
        public static final String PATH = RecipeStepDatabase.TABLE_NAME;
        @ContentUri(
                path = PATH,
                type = "vnd.android.cursor.dir/" + PATH,
                defaultSort = RecipeStepContract.COLUMN_STEP_ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    }
}
