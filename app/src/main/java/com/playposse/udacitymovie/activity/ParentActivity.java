package com.playposse.udacitymovie.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playposse.udacitymovie.R;
import com.playposse.udacitymovie.util.FontUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A parent class for activities. It adds a navigation drawer. The actual content is specified
 * by calling {@link #addMainFragment(Fragment)}.
 */
public abstract class ParentActivity extends AppCompatActivity {

    private static final String LOG_TAG = ParentActivity.class.getSimpleName();

    private static final String MAIN_FRAGMENT_TAG = "mainFragment";

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.main_fragment_container) LinearLayout mainFragmentContainer;
    @BindView(R.id.navigation_view) NavigationView navigationView;
    @BindView(R.id.my_toolbar) Toolbar toolbar;

    private ActionBarDrawerToggle drawerToggle;

    /**
     * Initializes the navigation drawer.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent);
        ButterKnife.bind(this);

        initNavigationDrawer();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    private void initNavigationDrawer() {
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });

        // Set custom font on navigation header
        View headerView = navigationView.getHeaderView(0);
        TextView appTitleTextView = headerView.findViewById(R.id.app_title_text_view);
        FontUtil.apply(appTitleTextView, FontUtil.YELLOWTAIL_FONT);
    }

    protected void addMainFragment(Fragment mainFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentByTag(MAIN_FRAGMENT_TAG) == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.main_fragment_container, mainFragment, MAIN_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((drawerToggle != null) && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                    return true;
                }
                break;
            case R.id.discover_most_popular_menu_item:
            case R.id.discover_highest_rated_menu_item:
            case R.id.discover_highest_grossing_menu_item:
                drawerLayout.closeDrawers();
                finish();
                ActivityNavigator.startDiscoverActivity(this, item.getItemId());
                return true;
            case R.id.favorite_menu_item:
                drawerLayout.closeDrawers();
                finish();
                ActivityNavigator.startFavoriteActivity(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
