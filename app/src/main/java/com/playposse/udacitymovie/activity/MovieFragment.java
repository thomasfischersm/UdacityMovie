package com.playposse.udacitymovie.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.playposse.udacitymovie.R;
import com.playposse.udacitymovie.data.MovieContentContract.MovieQuery;
import com.playposse.udacitymovie.data.MovieContentContract.MovieTable;
import com.playposse.udacitymovie.util.MediaUrlBuilder;
import com.playposse.udacitymovie.util.SmartCursor;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link Fragment} that shows movie details.
 *
 * <p>The fragment looks in the {@link Intent} for the movie id.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String MOVIE_ID = "movieId";
    private static final int LOADER_MANAGER_ID = 2;

    @BindView(R.id.backdrop_image_view) ImageView backdropImageView;
    @BindView(R.id.title_text_view) TextView titleTextView;
    @BindView(R.id.tagline_text_view) TextView taglineTextView;
    @BindView(R.id.poster_image_view) ImageView posterImageView;
    @BindView(R.id.release_year_text_view) TextView releaseYearTextView;
    @BindView(R.id.runtime_text_view) TextView runtimeTextView;
    @BindView(R.id.rating_text_view) TextView ratingTextView;
    @BindView(R.id.overview_text_layout) TextView overviewTextView;

    private long movieId;

    public static MovieFragment newInstance(Intent intent) {
        long movieId = ActivityNavigator.getMovieId(intent);

        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        args.putLong(MOVIE_ID, movieId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            movieId = getArguments().getLong(MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_MANAGER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                MovieQuery.CONTENT_URI,
                MovieQuery.COLUMN_NAMES,
                null,
                new String[]{Long.toString(movieId)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToNext()) {
            SmartCursor smartCursor = new SmartCursor(cursor, MovieQuery.COLUMN_NAMES);

            String title = smartCursor.getString(MovieTable.TITLE_COLUMN);
            String tagline = smartCursor.getString(MovieTable.TAGLINE_COLUMN);
            String backdropPath = smartCursor.getString(MovieTable.BACKDROP_PATH_COLUMN);
            String backdropUrl = MediaUrlBuilder.buildBackdropUrl(backdropPath);
            String posterPath = smartCursor.getString(MovieTable.POSTER_PATH_COLUMN);
            String posterUrl = MediaUrlBuilder.buildPosterUrl(posterPath);
            String releaseYear = smartCursor.getString(MovieTable.RELEASE_YEAR_COLUMN);
            String runtime = smartCursor.getString(MovieTable.RUNTIME_COLUMN);
            String rating = smartCursor.getString(MovieTable.VOTE_AVERAGE_COLUMN);
            String overview = smartCursor.getString(MovieTable.OVERVIEW_COLUMN);

            titleTextView.setText(title);
            taglineTextView.setText(tagline);
            releaseYearTextView.setText(releaseYear);
            runtimeTextView.setText(runtime);
            ratingTextView.setText(rating);
            overviewTextView.setText(overview);

            Glide.with(this)
                    .load(backdropUrl)
                    .into(backdropImageView);

            Glide.with(this)
                    .load(posterUrl)
                    .into(posterImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Ignore this. Let the user see the stale data.
    }
}
