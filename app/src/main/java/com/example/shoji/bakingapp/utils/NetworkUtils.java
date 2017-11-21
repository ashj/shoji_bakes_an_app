package com.example.shoji.bakingapp.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import timber.log.Timber;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        Timber.d("isNetworkConnected= %b",((networkInfo != null) &&
                (networkInfo.isConnectedOrConnecting())));

        return (networkInfo != null) &&
                (networkInfo.isConnectedOrConnecting());
    }

    public static String getDataFromUrlString(String urlString) {
        String jsonString = null;

        try {
            URL url = simpleURLBuilder(urlString);
            if (url != null)
                jsonString = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (MalformedURLException mfurle) {
            Timber.e(mfurle.getMessage());
        } catch (IOException ioe) {
            Timber.e(ioe.getMessage());
            return null;
        }

        return jsonString;

    }

    private static URL simpleURLBuilder(String urlString) throws MalformedURLException {
        Uri builtUri = Uri.parse(urlString).buildUpon()
                .build();

        URL url = new URL(builtUri.toString());

        Timber.d("simpleURLBuilder - Built URL: %s", url);

        return url;
    }

}
