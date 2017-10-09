package com.playposse.udacitymovie.services;

import com.playposse.udacitymovie.Secrets;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * A utility with queries to the Movie DB API.
 */
public final class MovieDbApiQueries {

    private MovieDbApiQueries() {
    }

    public static MovieResultsPage getDiscoveryList(String sortByFilter) {
        TmdbApi tmdbApi = new TmdbApi(Secrets.getMovieApiKeyV3());
        Discover discover = new Discover();
        discover.sortBy("popularity.desc");
        return tmdbApi.getDiscover().getDiscover(discover);
    }
}
