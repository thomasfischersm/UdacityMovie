package com.playposse.udacitymovie.service;

import android.support.annotation.NonNull;

import com.playposse.udacitymovie.Secrets;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbMovies.MovieMethod;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * A utility with queries to the Movie DB API.
 */
final class MovieDbApiQueries {

    private static final String LANGUAGE = "en";
    private static final int INITIAL_PAGE = 1;

    private MovieDbApiQueries() {
    }

    @NonNull
    private static TmdbApi getTmdbApi() {
        return new TmdbApi(Secrets.getMovieApiKeyV3());
    }

    static MovieResultsPage getDiscoveryList(String sortByFilter) {
        Discover discover = new Discover();
        discover.sortBy(sortByFilter);
        return getTmdbApi().getDiscover().getDiscover(discover);
    }

    static MovieResultsPage getTopRatedMovies() {
        return getTmdbApi().getMovies().getTopRatedMovies(LANGUAGE, INITIAL_PAGE);
    }

    static MovieResultsPage getPopularMovies() {
        return getTmdbApi().getMovies().getPopularMovies(LANGUAGE, INITIAL_PAGE);
    }

    static MovieDb getExtendedMovieInfo(long movieId) {
        TmdbMovies movies = getTmdbApi().getMovies();
        return movies.getMovie(
                (int) movieId,
                LANGUAGE,
                MovieMethod.credits,
                MovieMethod.images,
                MovieMethod.keywords,
                MovieMethod.reviews,
                MovieMethod.similar,
                MovieMethod.videos);
    }
}