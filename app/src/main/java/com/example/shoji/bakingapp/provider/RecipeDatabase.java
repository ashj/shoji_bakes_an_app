package com.example.shoji.bakingapp.provider;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = RecipeDatabase.VERSION)
public class RecipeDatabase {
    public static final int VERSION = 1;

    @Table(RecipeContract.class)
    public static final String TABLE_NAME = "recipes";
}
