package com.example.shoji.bakingapp.utils;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;

public class LoaderCallBackUtils {
    public static void initOrRestartLoader(int loaderId,
                                       Bundle args,
                                       LoaderManager loaderManager,
                                       LoaderManager.LoaderCallbacks callback) {

        if(null == loaderManager.getLoader(loaderId)) {
            loaderManager.initLoader(loaderId,
                    args, callback);
        }
        else {
            loaderManager.restartLoader(loaderId,
                    args, callback);
        }
    }
}
