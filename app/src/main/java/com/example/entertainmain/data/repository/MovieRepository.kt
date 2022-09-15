package com.example.entertainmain.data.repository

import com.example.entertainmain.data.model.Movie
import com.example.entertainmain.data.model.WatchList
import com.example.entertainmain.data.source.api.RetrofitInstance
import com.example.entertainmain.data.source.db.AppDatabase

class MovieRepository(
    val db: AppDatabase
) {
    suspend fun getTrendingMovies(pageNo: Int) = RetrofitInstance.movieApi.getTrendingMovies(pageNo)

    suspend fun getPopularMovies(pageNo: Int) = RetrofitInstance.movieApi.getPopularMovies(pageNo)

    suspend fun getTopRatedMovies(pageNo: Int) = RetrofitInstance.movieApi.getTopRatedMovies(pageNo)

    suspend fun searchMovies(title: String, pageNo: Int) = RetrofitInstance.movieApi.searchMovieByTitle(title, pageNo)

    suspend fun getCarasouelMovies() = RetrofitInstance.movieApi.getCarasouelMovies()

    suspend fun getCastInfoForMovieId(movieId: String) = RetrofitInstance.movieApi.getCastInfoForMovieId(movieId)

    suspend fun insertMovieIntoFavorites(movie: Movie) = db.getMovieDao().upsert(movie)

    fun getFavoriteMovies() = db.getMovieDao().getAllFavoriteMovies()

    fun getFavoriteMovieById(movieId: String) = db.getMovieDao().getFavoriteMovieById(movieId)

    suspend fun addToWatchlist(watchList: WatchList) = db.getWatchlistDao().upsertWatchList(watchList)

    fun getWatchlistToWatch() = db.getWatchlistDao().getWatchListUnwatched()

    fun getWatchlistCompleted() = db.getWatchlistDao().getWatchListWatched()

    suspend fun deleteWatchlist(watchList: WatchList) = db.getWatchlistDao().deleteWatchList(watchList)
}