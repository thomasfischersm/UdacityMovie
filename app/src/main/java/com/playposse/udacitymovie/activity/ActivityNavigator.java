package com.playposse.udacitymovie.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.playposse.udacitymovie.activity.DiscoverActivity.DiscoveryCategory;

/**
 * A utility that helps navigating between activities.
 */
public final class ActivityNavigator {

    private static final String LOG_TAG = ActivityNavigator.class.getSimpleName();

    private ActivityNavigator() {}

    public static void startDiscoverActivity(Context context, int menuResId) {
        startDiscoverActivity(context, getDiscoveryCategory(menuResId));
    }

    private static void startDiscoverActivity(
            Context context,
            DiscoveryCategory discoveryCategory) {

        Intent intent = new Intent(context, DiscoverActivity.class);
        intent.putExtra(
                DiscoverActivity.DISCOVER_CATEGORY_EXTRA_CONSTANT,
                discoveryCategory.getMenuResId());
        context.startActivity(intent);
    }

    public static DiscoveryCategory getDiscoveryCategory(Intent intent) {
        int menuResId = intent.getIntExtra(
                DiscoverActivity.DISCOVER_CATEGORY_EXTRA_CONSTANT,
                DiscoverActivity.DEFAULT_CATEGORY);

        return getDiscoveryCategory(menuResId);
    }

    @NonNull
    public static DiscoveryCategory getDiscoveryCategory(int menuResId) {
        for (DiscoveryCategory category : DiscoveryCategory.values()) {
            if (category.getMenuResId() == menuResId) {
                return category;
            }
        }

        throw new IllegalStateException("Failed to find discover category: " + menuResId);
    }
}
