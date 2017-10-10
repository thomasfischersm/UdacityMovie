package com.playposse.udacitymovie.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.playposse.udacitymovie.R;
import com.playposse.udacitymovie.data.MovieContentContract.FavoriteQuery;

/**
 * A {@link Fragment} that shows the user's favorite movies. It cleverly extends the
 * {@link DiscoverFragment} to reuse the view while only overwriting the query.
 */
public class FavoriteFragment extends DiscoverFragment {

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.favorite_activity_title);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                FavoriteQuery.CONTENT_URI,
                null, null,
                null,
                null);
    }
}
