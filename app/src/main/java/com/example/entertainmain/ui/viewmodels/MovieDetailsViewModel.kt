package com.example.entertainmain.ui.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.entertainmain.R
import com.example.entertainmain.data.model.CastInfoResponse
import com.example.entertainmain.data.model.Movie
import com.example.entertainmain.data.model.PrimaryImage
import com.example.entertainmain.data.model.WatchList
import com.example.entertainmain.data.repository.MovieRepository
import com.example.entertainmain.ui.MyApplication
import com.example.entertainmain.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MovieDetailsViewModel(
    app: Application,
    private val movieRepository: MovieRepository,
    movieId: String = ""
) :
    AndroidViewModel(app) {

    var currentMovieId: String = movieId

    val currentCastInfo: MutableLiveData<Resource<CastInfoResponse>> = MutableLiveData()

    init {
        currentMovieId = movieId
        if (movieId != "") {
            getMovieCastInfo(movieId)
        }
    }

    fun getMovieCastInfo(movieId: String) = viewModelScope.launch {
        currentMovieId = movieId
        currentCastInfo.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = movieRepository.getCastInfoForMovieId(movieId)
                currentCastInfo.postValue(handleCastInfoResponse(response))
            } else {
                currentCastInfo.postValue(Resource.Error("No internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> currentCastInfo.postValue(Resource.Error("Network Failure"))
                else -> currentCastInfo.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleCastInfoResponse(response: Response<CastInfoResponse>): Resource<CastInfoResponse> {
        if (response.isSuccessful) {
            response.body()?.let { castInfoResponse ->
                return Resource.Success(castInfoResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun addMovieToFavorites(movie: Movie, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            movie.primaryImage?.url?.let { url ->
                val hello = async {
                    val bitmap = getBitmapFromUrl(url, context)
                    bitmap?.let {
                        val imageLocation = saveBitmapToExternalDirectory(it, movie.id + ".jpg")
                        if (imageLocation != null) {
                            movie.savedImageFilePath = imageLocation
                        }
                    }
                    movie
                }
                movieRepository.insertMovieIntoFavorites(hello.await())
            }
        }
    }


    fun upsertWatchlist(watchList: WatchList) = viewModelScope.launch {
        movieRepository.addToWatchlist(watchList)
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<MyApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_ETHERNET -> true
                    TYPE_MOBILE -> true
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
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        return false
    }
}


@Suppress("UNCHECKED_CAST")
class MovieDetailViewModelProviderFactory(
    private val app: Application,
    private val repository: MovieRepository,
    private val movieId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MovieDetailsViewModel(app, repository, movieId) as T
    }
}