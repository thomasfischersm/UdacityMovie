package com.playposse.udacitymovie.services;

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

    private MovieDbApiQueries() {
    }

    @NonNull
    private static TmdbApi getTmdbApi() {
        return new TmdbApi(Secrets.getMovieApiKeyV3());
    }

    static MovieResultsPage getDiscoveryList(String sortByFilter) {
        Discover discover = new Discover();
        discover.sortBy("popularity.desc");
        return getTmdbApi().getDiscover().getDiscover(discover);
    }

    static MovieDb getExtendedMovieInfo(long movieId) {
        TmdbMovies movies = getTmdbApi().getMovies();
        return movies.getMovie(
                (int) movieId,
                "en",
                MovieMethod.credits,
                MovieMethod.images,
                MovieMethod.keywords,
                MovieMethod.reviews,
                MovieMethod.similar,
                MovieMethod.videos);
    }
}