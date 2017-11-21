package com.example.shoji.bakingapp.ui;

import android.os.Bundle;

import com.example.shoji.bakingapp.BuildConfig;
import com.example.shoji.bakingapp.R;

import timber.log.Timber;

public class MainActivity extends AppCompatActivityEx {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
        Timber.d("Logging With Timber");

    }
}
