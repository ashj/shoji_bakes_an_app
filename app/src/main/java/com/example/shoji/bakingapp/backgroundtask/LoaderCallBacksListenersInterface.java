package com.example.shoji.bakingapp.backgroundtask;

import android.content.Context;
import android.os.Bundle;

public interface LoaderCallBacksListenersInterface<Result> {
    void onStartLoading(Context context);
    Result onLoadInBackground(Context context, Bundle args);
    void onLoadFinished(Context context, Result result);
}
