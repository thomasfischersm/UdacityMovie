package com.playposse.udacitymovie.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playposse.udacitymovie.R;
import com.playposse.udacitymovie.activity.DiscoverActivity.DiscoveryCategory;

/**
 * A {@link Fragment} to show a grid of movies filtered by a discovery category.
 */
public class DiscoverFragment extends Fragment {

    private static final String CATEGORY_MENU_RES_ID = "categoryMenuResId";

    private DiscoveryCategory discoveryCategory;

    /**
     * Creates a new instance of this Fragment.
     */
    public static DiscoverFragment newInstance(DiscoveryCategory discoveryCategory) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putInt(CATEGORY_MENU_RES_ID, discoveryCategory.getMenuResId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int menuResId = getArguments().getInt(CATEGORY_MENU_RES_ID);
            discoveryCategory = ActivityNavigator.getDiscoveryCategory(menuResId);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }
}
