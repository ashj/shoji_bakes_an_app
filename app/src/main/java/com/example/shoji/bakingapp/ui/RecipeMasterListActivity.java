package com.example.shoji.bakingapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.shoji.bakingapp.R;

public class RecipeMasterListActivity extends AppCompatActivity {
    public static final String EXTRA_RECIPE_DATA = "extra-recipe-data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_master_list);
    }
}
