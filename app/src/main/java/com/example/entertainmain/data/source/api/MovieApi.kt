package com.example.entertainmain.data.source.api


import com.example.entertainmain.data.model.CastInfoResponse
import com.example.entertainmain.data.model.MoviesListResponse
import com.example.entertainmain.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("/titles")
    suspend fun getTrendingMovies(
        @Query("page") page: Int = 1,
        @Query("list") list: String = Constants.MOVIE_TRENDING_LAST_WEEK,
        @Query("info") info: String = Constants.MOVIE_INFO,
        @Query("titleType") titleType: String = "movie",
        @Query("sort") sort: String = "year.decr",
        @Query("limit") limit: Int = Constants.MOVIES_PER_PAGE
        ): Response<MoviesListResponse>

    @GET("/titles")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 1,
        @Query("list") list: String = Constants.MOVIE_TOP_RATED_250,
        @Query("info") info: String = Constants.MOVIE_INFO,
        @Query("titleType") titleType: String = "movie",
        @Query("limit") limit: Int = Constants.MOVIES_PER_PAGE
//        @Query("sort") sort: String = "year.incr",
    ): Response<MoviesListResponse>

    @GET("/titles")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
        @Query("list") list: String = Constants.MOVIE_POPULAR,
        @Query("info") info: String = Constants.MOVIE_INFO,
        @Query("titleType") titleType: String = "movie",
        @Query("limit") limit: Int = Constants.MOVIES_PER_PAGE
//        @Query("sort") sort: String = "year.decr",
    ): Response<MoviesListResponse>


    @GET("/titles/x/titles-by-ids")
    suspend fun getCarasouelMovies(
        @Query("idsList") movieIds: List<String> = listOf(
            "tt9263550",
            "tt0468569",
            "tt5700672",
            "tt0245429",
            "tt0816692",
            "tt1375666",
            "tt0986264"
        ),
        @Query("info") info: String = Constants.MOVIE_INFO
    ): Response<MoviesListResponse>

    @GET("/titles/{id}")
    suspend fun getCastInfoForMovieId(
        @Path("id") titleId: String,
        @Query("info") info: String = Constants.MOVIES_CAST_INFO
    ): Response<CastInfoResponse>

    @GET("/titles/search/title/{query}")
    suspend fun searchMovieByTitle(
        @Path("query") query: String,
        @Query("page") page: Int = 1,
        @Query("info") info: String = Constants.MOVIE_INFO,
        @Query("titleType") titleType: String = "movie",
        @Query("limit") limit: Int = Constants.MOVIES_PER_PAGE
    ): Response<MoviesListResponse>

}