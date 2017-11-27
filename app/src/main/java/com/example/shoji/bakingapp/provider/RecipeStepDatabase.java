package com.example.shoji.bakingapp.provider;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = RecipeStepDatabase.VERSION)
public class RecipeStepDatabase {
    public static final int VERSION = 1;

    @Table(RecipeStepContract.class)
    public static final String TABLE_NAME = "recipe_steps";
}
