package com.example.entertainmain.data.source.api

import com.example.entertainmain.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitInstance {
    private val headersInterceptor = Interceptor { chain ->
        val request = chain.request()
            .newBuilder()
            .addHeader("X-RapidAPI-Key", Constants.MOVIE_API_KEY)
            .addHeader("X-RapidAPI-Host", Constants.MOVIE_API_HOST)
            .build()
        chain.proceed(request)
    }

    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(headersInterceptor)
            .build()
        Retrofit.Builder()
            .baseUrl(Constants.MOVIE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val movieApi by lazy {
        retrofit.create(MovieApi::class.java)
    }
}