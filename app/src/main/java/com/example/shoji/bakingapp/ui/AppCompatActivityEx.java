package com.example.shoji.bakingapp.ui;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;


public abstract class AppCompatActivityEx extends AppCompatActivity {
    protected void showSnackbar(int rootViewId, int string_resId, int length) {
        Snackbar snackbar = Snackbar.make(findViewById(rootViewId),
                string_resId,
                length);

        snackbar.show();
    }

}
