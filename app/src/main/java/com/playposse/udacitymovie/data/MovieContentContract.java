package com.playposse.udacitymovie.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.playposse.udacitymovie.R;

import org.apache.commons.lang3.ArrayUtils;

/**
 * A contract for the {@link MovieContentProvider}.
 */
public class MovieContentContract {

    public static final String AUTHORITY = "com.playposse.udacitymovie.provider";

    private static final String CONTENT_SCHEME = "content";

    private static Uri createContentUri(String path) {
        return new Uri.Builder()
                .scheme(CONTENT_SCHEME)
                .encodedAuthority(AUTHORITY)
                .appendPath(path)
                .build();
    }

    /**
     * Stores meta information about movies.
     */
    public static final class MovieTable implements BaseColumns {

        public static final String PATH = "movie";
        public static final Uri CONTENT_URI = createContentUri(PATH);
        public static final String TABLE_NAME = "movie";

        public static final String ID_COLUMN = _ID;
        public static final String HAS_EXTENDED_INFO_COLUMN = "has_extended_info";
        public static final String IMDB_ID_COLUMN = "imdb_id";
        public static final String TITLE_COLUMN = "title";
        public static final String TAGLINE_COLUMN = "tagline";
        public static final String USER_RATING_COLUMN = "user_rating";
        public static final String VOTE_AVERAGE_COLUMN = "vote_average";
        public static final String OVERVIEW_COLUMN = "overview";
        public static final String POSTER_PATH_COLUMN = "poster_path";
        public static final String BACKDROP_PATH_COLUMN = "backdrop_path";
        public static final String RELEASE_YEAR_COLUMN = "release_year";
        public static final String RUNTIME_COLUMN = "runtime";

        public static final String[] COLUMN_NAMES = new String[]{
                ID_COLUMN,
                HAS_EXTENDED_INFO_COLUMN,
                IMDB_ID_COLUMN,
                TITLE_COLUMN,
                TAGLINE_COLUMN,
                USER_RATING_COLUMN,
                VOTE_AVERAGE_COLUMN,
                OVERVIEW_COLUMN,
                POSTER_PATH_COLUMN,
                BACKDROP_PATH_COLUMN,
                RELEASE_YEAR_COLUMN,
                RUNTIME_COLUMN};

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE movie "
                        + "(_id INTEGER PRIMARY KEY, "
                        + "has_extended_info INTEGER DEFAULT 0, "
                        + "imdb_id TEXT, "
                        + "title TEXT, "
                        + "tagline TEXT, "
                        + "user_rating REAL, "
                        + "vote_average REAL, "
                        + "overview TEXT, "
                        + "poster_path TEXT, "
                        + "backdrop_path TEXT, "
                        + "release_year TEXT, "
                        + "runtime INTEGER)";
    }

    /**
     * Stores meta information about discover lists.
     */
    public static final class DiscoveryListTable implements BaseColumns {

        public static final String PATH = "discoverylist";
        public static final Uri CONTENT_URI = createContentUri(PATH);
        public static final String TABLE_NAME = "discovery_list";

        public static final String ID_COLUMN = _ID;
        public static final String UI_LABEL_RES_ID_COLUMN = "ui_label_res_id";
        public static final String SORT_BY_FILTER_COLUMN = "sort_by_filter";
        public static final String LAST_RETRIEVED_COLUMN = "last_retrieved";

        public static final String[] COLUMN_NAMES = new String[]{
                ID_COLUMN,
                UI_LABEL_RES_ID_COLUMN,
                SORT_BY_FILTER_COLUMN,
                LAST_RETRIEVED_COLUMN};

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE discovery_list "
                        + "(_id INTEGER PRIMARY KEY, "
                        + "ui_label_res_id INTEGER, "
                        + "sort_by_filter TEXT, "
                        + "last_retrieved INTEGER)";

        static final String SQL_CREATE_DATA =
                "INSERT INTO discovery_list (ui_label_res_id, sort_by_filter) " +
                        "values (" + R.string.most_popular_category + ", 'popularity.desc');" +
                        "INSERT INTO discovery_list (ui_label_res_id, sort_by_filter) " +
                        "values (" + R.string.highest_rated_category + ", 'vote_average.desc');" +
                        "INSERT INTO discovery_list (ui_label_res_id, sort_by_filter) " +
                        "values (" + R.string.highest_grossing_category + ", 'revenue.desc');";
    }

    /**
     * Stores the join between the discovery list and the movies.
     */
    public static final class DiscoverListMovieTable implements BaseColumns {

        public static final String PATH = "discoverylistmovie";
        public static final Uri CONTENT_URI = createContentUri(PATH);
        public static final String TABLE_NAME = "discovery_list_movie";

        public static final String ID_COLUMN = _ID;
        public static final String DISCOVERY_LIST_ID_COLUMN = "discovery_list_id";
        public static final String MOVIE_ID_COLUMN = "movie_id";

        public static final String[] COLUMN_NAMES = new String[]{
                ID_COLUMN,
                DISCOVERY_LIST_ID_COLUMN,
                MOVIE_ID_COLUMN};

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE discovery_list_movie "
                        + "(_id INTEGER PRIMARY KEY, "
                        + "discovery_list_id INTEGER, "
                        + "movie_id INTEGER," +
                        "FOREIGN KEY(discovery_list_id) REFERENCES discovery_list(_id)," +
                        "FOREIGN KEY(movie_id) REFERENCES movie(_id))";
    }

    /**
     * Stores the movie reviews.
     */
    public static final class MovieReviewTable implements BaseColumns {

        public static final String PATH = "moviereview";
        public static final Uri CONTENT_URI = createContentUri(PATH);
        public static final String TABLE_NAME = "movie_review";

        public static final String ID_COLUMN = _ID;
        public static final String MOVIE_ID_COLUMN = "movie_id";
        public static final String AUTHOR_COLUMN = "author";
        public static final String CONTENT_COLUMN = "content";
        public static final String URL_COLUMN = "url";

        public static final String[] COLUMN_NAMES = new String[]{
                ID_COLUMN,
                MOVIE_ID_COLUMN,
                AUTHOR_COLUMN,
                CONTENT_COLUMN,
                URL_COLUMN};

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE movie_review "
                        + "(_id INTEGER PRIMARY KEY, "
                        + "movie_id INTEGER, "
                        + "author TEXT, "
                        + "content TEXT, "
                        + "url TEXT, "
                        + "FOREIGN KEY(movie_id) REFERENCES movie(_id))";
    }

    /**
     * Stores the movie videos.
     */
    public static final class MovieVideoTable implements BaseColumns {

        public static final String PATH = "movievideo";
        public static final Uri CONTENT_URI = createContentUri(PATH);
        public static final String TABLE_NAME = "movie_video";

        public static final String ID_COLUMN = _ID;
        public static final String MOVIE_ID_COLUMN = "movie_id";
        public static final String SITE_COLUMN = "site";
        public static final String KEY_COLUMN = "key";
        public static final String TYPE_COLUMN = "type";

        public static final String[] COLUMN_NAMES = new String[]{
                ID_COLUMN,
                MOVIE_ID_COLUMN,
                SITE_COLUMN,
                KEY_COLUMN,
                TYPE_COLUMN};

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE movie_video "
                        + "(_id INTEGER PRIMARY KEY, "
                        + "movie_id INTEGER, "
                        + "site TEXT, "
                        + "key TEXT, "
                        + "type TEXT, "
                        + "FOREIGN KEY(movie_id) REFERENCES movie(_id))";
    }

    /**
     * A query that returns a list of movies that are inside of a category.
     */
    public static final class DiscoveryCategoryQuery implements BaseColumns {
        public static final String PATH = "discoverycategoryquery";
        public static final Uri CONTENT_URI = createContentUri(PATH);

        public static final String[] COLUMN_NAMES = MovieTable.COLUMN_NAMES;

        static final String SQL_QUERY =
                "select movie.*, favorite._id not null as is_favorite " +
                        "from discovery_list " +
                        "join discovery_list_movie " +
                        "on discovery_list._id=discovery_list_movie.discovery_list_id " +
                        "join movie " +
                        "on discovery_list_movie.movie_id=movie._id " +
                        "left join favorite " +
                        "on movie._id=favorite.movie_id " +
                        "where discovery_list.ui_label_res_id=?";
    }

    /**
     * Stores the favorite movies of the user.
     */
    public static final class FavoriteTable implements BaseColumns {

        public static final String PATH = "favorite";
        public static final Uri CONTENT_URI = createContentUri(PATH);
        public static final String TABLE_NAME = "favorite";

        public static final String ID_COLUMN = _ID;
        public static final String MOVIE_ID_COLUMN = "movie_id";

        public static final String[] COLUMN_NAMES = new String[]{
                ID_COLUMN,
                MOVIE_ID_COLUMN};

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE favorite "
                        + "(_id INTEGER PRIMARY KEY, "
                        + "movie_id INTEGER, "
                        + "FOREIGN KEY(movie_id) REFERENCES movie(_id))";
    }

    /**
     * A query that combines all the movie data. Right now the use is to add the favorites flag to
     * it.
     */
    public static final class MovieQuery implements BaseColumns {

        public static final String PATH = "moviequery";
        public static final Uri CONTENT_URI = createContentUri(PATH);

        public static final String IS_FAVORITE_COLUMN = "is_favorite";
        public static final String[] COLUMN_NAMES =
                ArrayUtils.addAll(MovieTable.COLUMN_NAMES, IS_FAVORITE_COLUMN);

        static final String SQL_QUERY =
                "select movie.*, favorite._id not null as is_favorite " +
                        "from movie " +
                        "left join favorite " +
                        "on movie._id=favorite.movie_id " +
                        "where movie._id=?";
    }


    /**
     * A query returns all the favorite movies.
     */
    public static final class FavoriteQuery implements BaseColumns {

        public static final String PATH = "favoritequery";
        public static final Uri CONTENT_URI = createContentUri(PATH);

        public static final String[] COLUMN_NAMES = MovieTable.COLUMN_NAMES;

        static final String SQL_QUERY =
                "select movie.* " +
                        "from movie " +
                        "join favorite " +
                        "on movie._id=favorite.movie_id";
    }
}
