package com.playposse.udacitymovie.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.playposse.udacitymovie.R;
import com.playposse.udacitymovie.activity.ActivityNavigator;
import com.playposse.udacitymovie.activity.DiscoverActivity;
import com.playposse.udacitymovie.data.ContentProviderQueries;
import com.playposse.udacitymovie.service.BuildMovieCacheService;
import com.playposse.udacitymovie.util.ToastUtil;

/**
 * A {@link BroadcastReceiver} that checks for network changes.
 */
public class NetworkReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = NetworkReceiver.class.getSimpleName();

    private Boolean hasNetwork = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo =
                    intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            Boolean hadNetwork = hasNetwork;
            if (networkInfo == null) {
                Log.e(LOG_TAG, "onReceive: Missing NetworkInfo!");
                hasNetwork = false;
            } else if (networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                hasNetwork = true;
            } else if (networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                hasNetwork = false;
            } else {
                Log.e(
                        LOG_TAG,
                        "onReceive: Unexpected network state: " + networkInfo.getDetailedState());
            }

            if ((hadNetwork != null) && (hadNetwork != hasNetwork)) {
                // Detected a network change.
                if (ContentProviderQueries.areDiscoveryListsCached(context)) {
                    // If the data is cached simply send the user a toast  to inform about using
                    // fresh vs. cached data.
                    if (hasNetwork) {
                        ToastUtil.sendShortToast(context, R.string.network_restored_toast);
                    } else {
                        ToastUtil.sendShortToast(context, R.string.network_lost_toast);
                    }
                } else {
                    // If there is no data cache, park the user on a no network activity until th
                    // network returns.
                    if (hasNetwork) {
                        context.startService(new Intent(context, BuildMovieCacheService.class));

                        int menuResId = DiscoverActivity.DiscoveryCategory.mostPopular.getMenuResId();
                        ActivityNavigator.startDiscoverActivity(context, menuResId);
                    } else {
                        ActivityNavigator.startNoNetworkActivity(context);
                    }
                }
            }

            if ((hadNetwork == null) && (!hasNetwork)) {
                // No network on startup.
                ActivityNavigator.startNoNetworkActivity(context);
            }
        }
    }
}
