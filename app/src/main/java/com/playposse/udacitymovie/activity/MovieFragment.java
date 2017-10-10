package com.playposse.udacitymovie.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.playposse.udacitymovie.R;
import com.playposse.udacitymovie.data.MovieContentContract.MovieQuery;
import com.playposse.udacitymovie.data.MovieContentContract.MovieReviewTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieTable;
import com.playposse.udacitymovie.data.MovieContentContract.MovieVideoTable;
import com.playposse.udacitymovie.util.MediaUrlBuilder;
import com.playposse.udacitymovie.util.RecyclerViewCursorAdapter;
import com.playposse.udacitymovie.util.SmartCursor;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link Fragment} that shows movie details.
 * <p>
 * <p>The fragment looks in the {@link Intent} for the movie id.
 */
public class MovieFragment extends Fragment {

    private static final String MOVIE_ID = "movieId";
    private static final int MOVIE_LOADER_MANAGER_ID = 2;
    private static final int VIDEO_LOADER_MANAGER_ID = 3;
    private static final int REVIEW_LOADER_MANAGER_ID = 4;
    private static final int GRID_SPAN = 1;

    @BindView(R.id.backdrop_image_view) ImageView backdropImageView;
    @BindView(R.id.title_text_view) TextView titleTextView;
    @BindView(R.id.tagline_text_view) TextView taglineTextView;
    @BindView(R.id.poster_image_view) ImageView posterImageView;
    @BindView(R.id.release_year_text_view) TextView releaseYearTextView;
    @BindView(R.id.runtime_text_view) TextView runtimeTextView;
    @BindView(R.id.rating_text_view) TextView ratingTextView;
    @BindView(R.id.overview_text_layout) TextView overviewTextView;
    @BindView(R.id.video_recycler_view) RecyclerView videoRecyclerView;
    @BindView(R.id.review_recycler_view) RecyclerView reviewRecyclerView;

    private long movieId;

    private VideoAdapter videoAdapter;
    private ReviewAdapter reviewAdapter;

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

        // Set up videos
        videoRecyclerView.setHasFixedSize(true);
        GridLayoutManager videoLayoutManager =
                new GridLayoutManager(getActivity(), GRID_SPAN, GridLayoutManager.HORIZONTAL, false);
        videoRecyclerView.setLayoutManager(videoLayoutManager);
        videoAdapter = new VideoAdapter();
        videoRecyclerView.setAdapter(videoAdapter);

        // Set up  reviews.
        reviewRecyclerView.setHasFixedSize(true);
        GridLayoutManager reviewLayoutManager =
                new GridLayoutManager(getActivity(), GRID_SPAN, GridLayoutManager.VERTICAL, false);
        reviewRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewAdapter = new ReviewAdapter();
        reviewRecyclerView.setAdapter(reviewAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(MOVIE_LOADER_MANAGER_ID, null, new MovieLoaderCallbacks());
        getLoaderManager().initLoader(VIDEO_LOADER_MANAGER_ID, null, new VideoLoaderCallbacks());
        getLoaderManager().initLoader(REVIEW_LOADER_MANAGER_ID, null, new ReviewLoaderCallbacks());
    }

    /**
     * A {@link LoaderCallbacks<Cursor>} that loads the movie meta information.
     */
    private class MovieLoaderCallbacks implements LoaderCallbacks<Cursor> {

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

                Glide.with(getActivity())
                        .load(backdropUrl)
                        .into(backdropImageView);

                Glide.with(getActivity())
                        .load(posterUrl)
                        .into(posterImageView);

                getActivity().setTitle(title);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // Ignore this. Let the user see the stale data.
        }
    }

    /**
     * A {@link LoaderCallbacks<Cursor>} that loads videos (e.g. trailers).
     */
    private class VideoLoaderCallbacks implements LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String where = MovieVideoTable.MOVIE_ID_COLUMN + "=? and "
                    + MovieVideoTable.SITE_COLUMN + "=?";
            return new CursorLoader(
                    getActivity(),
                    MovieVideoTable.CONTENT_URI,
                    MovieVideoTable.COLUMN_NAMES,
                    where,
                    new String[]{Long.toString(movieId), "YouTube"},
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            videoAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            videoAdapter.swapCursor(null);
        }
    }

    /**
     * A {@link LoaderCallbacks<Cursor>} that loads reviews.
     */
    private class ReviewLoaderCallbacks implements LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    getActivity(),
                    MovieReviewTable.CONTENT_URI,
                    MovieReviewTable.COLUMN_NAMES,
                    MovieReviewTable.MOVIE_ID_COLUMN + "=?",
                    new String[]{Long.toString(movieId)},
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            reviewAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            reviewAdapter.swapCursor(null);
        }
    }

    /**
     * An adapter that manages a list of videos (e.g. trailer).
     */
    private class VideoAdapter extends RecyclerViewCursorAdapter<VideoViewHolder> {

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.video_list_item,
                    parent,
                    false);
            return new VideoViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(VideoViewHolder holder, int position, Cursor cursor) {
            SmartCursor smartCursor = new SmartCursor(cursor, MovieVideoTable.COLUMN_NAMES);
            String type = smartCursor.getString(MovieVideoTable.TYPE_COLUMN);
            String key = smartCursor.getString(MovieVideoTable.KEY_COLUMN);
            final String youTubeUrl = MediaUrlBuilder.buildYouTubeUrl(key);

            holder.typeTextView.setText(type);

            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityNavigator.startYouTubeActivity(getActivity(), youTubeUrl);
                }
            });
        }
    }

    /**
     * A {@link RecyclerView.ViewHolder} for a video.
     */
    class VideoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root_view) LinearLayout rootView;
        @BindView(R.id.type_text_view) TextView typeTextView;

        private VideoViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * An adapter that manages a list of reviews.
     */
    private class ReviewAdapter extends RecyclerViewCursorAdapter<ReviewViewHolder> {

        @Override
        public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.review_list_item,
                    parent,
                    false);
            return new ReviewViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(ReviewViewHolder holder, int position, Cursor cursor) {
            SmartCursor smartCursor = new SmartCursor(cursor, MovieReviewTable.COLUMN_NAMES);

            String content = smartCursor.getString(MovieReviewTable.CONTENT_COLUMN);
            String author = smartCursor.getString(MovieReviewTable.AUTHOR_COLUMN);

            holder.contentTextView.setText(content);
            holder.authorTextView.setText(author);
        }
    }

    /**
     * A {@link RecyclerView.ViewHolder} for a video.
     */
    class ReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.content_text_view) TextView contentTextView;
        @BindView(R.id.author_text_view) TextView authorTextView;

        private ReviewViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
