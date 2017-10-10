package com.playposse.udacitymovie.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.playposse.udacitymovie.R;
import com.playposse.udacitymovie.activity.DiscoverActivity.DiscoveryCategory;
import com.playposse.udacitymovie.data.MovieContentContract.DiscoveryCategoryQuery;
import com.playposse.udacitymovie.data.MovieContentContract.MovieTable;
import com.playposse.udacitymovie.util.MediaUrlBuilder;
import com.playposse.udacitymovie.util.RecyclerViewCursorAdapter;
import com.playposse.udacitymovie.util.SmartCursor;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link Fragment} to show a grid of movies filtered by a discovery category.
 */
public class DiscoverFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String CATEGORY_MENU_RES_ID = "categoryMenuResId";
    private static final int GRID_SPAN = 3;
    private static final int LOADER_MANAGER = 1;

    @BindView(R.id.movie_recycler_view) RecyclerView movieRecyclerView;

    private DiscoveryCategory discoveryCategory;
    private MovieAdapter movieAdapter;

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

            // Set the title based on the discovery category.
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(discoveryCategory.getLabelResId());
            }
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_discovery, container, false);

        ButterKnife.bind(this, rootView);

        // Small performance improvement.
        movieRecyclerView.setHasFixedSize(true);

        movieRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), GRID_SPAN));
        movieAdapter = new MovieAdapter();
        movieRecyclerView.setAdapter(movieAdapter);

        getLoaderManager().initLoader(LOADER_MANAGER, null, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                DiscoveryCategoryQuery.CONTENT_URI,
                null,
                null,
                new String[]{Integer.toString(discoveryCategory.getLabelResId())},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        movieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

    /**
     * An adapter that manages a list of movies.
     */
    private class MovieAdapter extends RecyclerViewCursorAdapter<MovieViewHolder> {

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.movie_list_item,
                    parent,
                    false);
            return new MovieViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(MovieViewHolder holder, int position, Cursor cursor) {
            SmartCursor smartCursor = new SmartCursor(cursor, MovieTable.COLUMN_NAMES);
            final long movieId = smartCursor.getLong(MovieTable.ID_COLUMN);
            String posterPath = smartCursor.getString(MovieTable.POSTER_PATH_COLUMN);
            String posterUrl = MediaUrlBuilder.buildPosterUrl(posterPath);
            String title = smartCursor.getString(MovieTable.TITLE_COLUMN);
            Double voteAverage = smartCursor.getDouble(MovieTable.VOTE_AVERAGE_COLUMN);
            String voteAverageStr = getString(R.string.average_vote_format, voteAverage);

            if (posterPath != null) {
                Glide.with(getActivity())
                        .load(posterUrl)
                        .into(holder.posterImageView);
            }
            holder.titleTextView.setText(title);
            holder.voteAverageTextView.setText(voteAverageStr);

            holder.posterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityNavigator.startMovieActivity(getActivity(), movieId);
                }
            });
        }
    }

    /**
     * A {@link RecyclerView.ViewHolder} for a movie tile.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.poster_image_view) ImageView posterImageView;
        @BindView(R.id.title_text_view) TextView titleTextView;
        @BindView(R.id.vote_average_text_view) TextView voteAverageTextView;

        private MovieViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
