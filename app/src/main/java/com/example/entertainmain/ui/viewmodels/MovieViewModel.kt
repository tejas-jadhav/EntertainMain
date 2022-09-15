package com.example.entertainmain.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.*
import com.example.entertainmain.data.model.MoviesListResponse
import com.example.entertainmain.data.model.WatchList
import com.example.entertainmain.data.repository.MovieRepository
import com.example.entertainmain.ui.MyApplication
import com.example.entertainmain.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MovieViewModel(
    app: Application,
    private val movieRepository: MovieRepository
) : AndroidViewModel(app) {

    val trendingMovies: MutableLiveData<Resource<MoviesListResponse>> = MutableLiveData()
    var trendingMoviesPage = 1

    val popularMovies: MutableLiveData<Resource<MoviesListResponse>> = MutableLiveData()
    var popularMoviesPage = 1

    val topRatedMovies: MutableLiveData<Resource<MoviesListResponse>> = MutableLiveData()
    var topRatedMoviesResponse: MoviesListResponse? = null
    var topRatedMoviesPage = 1

    val carasouelMovies: MutableLiveData<Resource<MoviesListResponse>> = MutableLiveData()


    init {
        getCarasouelMovies()
        getTrendingMovies()
        getPopularMovies()
        getTopRatedMovies()

    }


    fun getTrendingMovies() = viewModelScope.launch(Dispatchers.IO) {
        safeApiCall(trendingMovies) {
            movieRepository.getTrendingMovies(trendingMoviesPage)
        }
    }


    fun getPopularMovies() = viewModelScope.launch {
        safeApiCall(popularMovies) {
            movieRepository.getPopularMovies(popularMoviesPage)
        }
    }

    fun getCarasouelMovies() = viewModelScope.launch {
        safeApiCall(carasouelMovies) {
            movieRepository.getCarasouelMovies()
        }
    }

    fun getTopRatedMovies() = viewModelScope.launch {
        topRatedMovies.postValue(Resource.Loading())
        try {
            val response = movieRepository.getTopRatedMovies(topRatedMoviesPage)
            topRatedMovies.postValue(handleTopRatedMoviesResponse(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> topRatedMovies.postValue(Resource.Error("Network Failure"))
                else -> topRatedMovies.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun getFavoriteMovies() = movieRepository.getFavoriteMovies()

    fun getWatchlistToWatch() = movieRepository.getWatchlistToWatch()

    fun getWatchlistWatched() = movieRepository.getWatchlistCompleted()

    fun upsertWatchlist(watchList: WatchList) = viewModelScope.launch {
        movieRepository.addToWatchlist(watchList)
    }

    fun deleteWatchlist(watchList: WatchList) = viewModelScope.launch {
        movieRepository.deleteWatchlist(watchList)
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MyApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        } else {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasCapability(NET_CAPABILITY_FOTA) -> true
                else -> false
            }
        }
        return false
    }

    private suspend fun safeApiCall(
        observable: MutableLiveData<Resource<MoviesListResponse>>,
        getResponse: suspend () -> Response<MoviesListResponse>
    ) {
        observable.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = getResponse()
                observable.postValue(handleMoviesResponse(response))
            } else {
                observable.postValue(Resource.Error("No internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> observable.postValue(Resource.Error("Network Failure"))
                else -> observable.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private fun handleMoviesResponse(response: Response<MoviesListResponse>): Resource<MoviesListResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleTopRatedMoviesResponse(response: Response<MoviesListResponse>): Resource<MoviesListResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                topRatedMoviesPage++
                if (topRatedMoviesResponse == null) {
                    topRatedMoviesResponse = resultResponse
                } else {
                    val oldMovies = topRatedMoviesResponse?.movies
                    val newMovies = resultResponse.movies
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(topRatedMoviesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

}

class MovieViewModelProviderFactory(
    private val application: Application,
    private val movieRepository: MovieRepository
) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MovieViewModel(application, movieRepository) as T
    }
}