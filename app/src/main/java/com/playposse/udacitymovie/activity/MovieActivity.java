package com.playposse.udacitymovie.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.playposse.udacitymovie.R;

/**
 * An {@link Activity} that shows the movie details.
 */
public class MovieActivity extends ParentActivity {

    public static final String MOVIE_ID_EXTRA_CONSTANT = "movieId";

    private ShareActionProvider shareActionProvider;
    private Intent shareIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addMainFragment(MovieFragment.newInstance(getIntent()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create action bar menu.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);

        // Store a handle to set the share intent later.
        MenuItem item = menu.findItem(R.id.menu_item_share);
        shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        shareActionProvider.setShareIntent(shareIntent);

        return true;
    }

    /**
     * Sets the share intent to point to the YouTube Url of the movie. This is called by the
     * fragment when it receives the data.
     */
    void setShareIntent(@Nullable String youTubeUrl) {
        if (youTubeUrl == null) {
            shareIntent = null;
            if (shareActionProvider != null) {
                shareActionProvider.setShareIntent(null);
            }
            return;
        }

        String subject = getString(R.string.share_subject);

        shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, youTubeUrl);

        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }
    }
}
