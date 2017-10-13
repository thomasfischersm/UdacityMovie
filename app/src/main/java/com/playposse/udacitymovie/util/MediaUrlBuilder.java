package com.playposse.udacitymovie.util;

/**
 * A helper that deals with building URLs for media assets from the Movie Database.
 */
public final class MediaUrlBuilder {

    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private static final String BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w1280";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_THUMBNAIL_BASE_URL = "http://img.youtube.com/vi/%1$s/0.jpg";

    private MediaUrlBuilder() {}

    public static String buildPosterUrl(String posterPath) {
        // Note: There is a more sophisticated approach that involves querying the API for the
        // base URL and available poster sizes.
        return POSTER_BASE_URL + posterPath;
    }

    public static String buildBackdropUrl(String posterPath) {
        // Note: There is a more sophisticated approach that involves querying the API for the
        // base URL and available poster sizes.
        return BACKDROP_BASE_URL + posterPath;
    }

    public static String buildYouTubeUrl(String key) {
        return YOUTUBE_BASE_URL + key;
    }

    public static String buildYouTubeThumbnailUrl(String key) {
        return String.format(YOUTUBE_THUMBNAIL_BASE_URL, key);
    }
}
