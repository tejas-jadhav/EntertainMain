package com.example.entertainmain.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MoviesListResponse(
    val entries: Int,
    @SerializedName("results")
    val movies: MutableList<Movie>,
    val next: String,
    val page: String
): Serializable