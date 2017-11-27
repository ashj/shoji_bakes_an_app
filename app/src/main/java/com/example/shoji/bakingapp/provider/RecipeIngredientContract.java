package com.example.shoji.bakingapp.provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public class RecipeIngredientContract {

    /* internal database id */
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String COLUMN_RECIPE_ID = "recipe_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_QUANTITY = "quantity";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_MEASURE = "measure";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_DESCRIPTION = "description";

}
