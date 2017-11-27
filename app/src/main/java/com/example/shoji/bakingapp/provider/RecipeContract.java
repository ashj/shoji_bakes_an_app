package com.example.shoji.bakingapp.provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public class RecipeContract {

    /* internal database id */
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    @AutoIncrement
    public static final String _ID = "_id";

    /* recipe id provided by network */
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_RECIPE_ID = "recipe_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_NAME = "name";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_SERVINGS = "servings";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_IMAGE = "image";

}
