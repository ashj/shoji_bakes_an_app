package com.example.shoji.bakingapp.provider;

import com.example.shoji.bakingapp.pojo.RecipeIngredient;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = RecipeIngredientDatabase.VERSION)
public class RecipeIngredientDatabase {
    public static final int VERSION = 1;

    @Table(RecipeIngredientContract.class)
    public static final String TABLE_NAME = "recipe_ingredients";
}
