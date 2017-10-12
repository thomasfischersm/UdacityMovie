package com.playposse.udacitymovie.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * A utility class to deal with network detection.
 */
public final class NetworkUtil {

    private static final String LOG_TAG = NetworkUtil.class.getSimpleName();

    private NetworkUtil() {}

    public static boolean isWifiActive(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            Log.e(LOG_TAG, "isWifiActive: Got NPE when checking for ConnectivityManger.");
            return false;
        }

        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }
}
