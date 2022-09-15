package com.example.entertainmain.ui.search.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.*
import com.example.entertainmain.data.model.MoviesListResponse
import com.example.entertainmain.data.repository.MovieRepository
import com.example.entertainmain.ui.MyApplication
import com.example.entertainmain.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class SearchViewModel(app: Application, private val repository: MovieRepository) :
    AndroidViewModel(app) {


    val searchResult: MutableLiveData<Resource<MoviesListResponse>> = MutableLiveData()
    private var searchResultResponse: MoviesListResponse? = null
    private var searchPageNo: Int = 1
    private val TAG = "SearchViewModel"
    private var previousQueryTitle: String? = null

    fun searchMovieByTitle(query: String) = viewModelScope.launch {
        searchResult.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                previousQueryTitle?.let {
                    if (it != query) {
                        searchPageNo = 1
                        searchResultResponse = null
                    }
                }
                previousQueryTitle = query
                val response = repository.searchMovies(query, searchPageNo)
                searchResult.postValue(handleSearchResponse(response))
            } else {

                searchResult.postValue(Resource.Error("No internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchResult.postValue(Resource.Error("No Internet Connection"))
                else -> searchResult.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleSearchResponse(response: Response<MoviesListResponse>): Resource<MoviesListResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchPageNo++
                if (searchResultResponse == null) {
                    searchResultResponse = resultResponse
                } else {
                    val oldSearchResults = searchResultResponse?.movies
                    val newSearchResults = resultResponse.movies

                    oldSearchResults?.addAll(newSearchResults)
                }
                return Resource.Success(searchResultResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
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
            val networkState = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(networkState) ?: return false

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }

        return false
    }
}

class SearchViewModelFactory(
    private val app: Application,
    private val repository: MovieRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(app, repository) as T
    }
}