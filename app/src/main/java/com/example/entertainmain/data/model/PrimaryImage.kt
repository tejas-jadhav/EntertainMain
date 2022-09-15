package com.example.entertainmain.data.model

import java.io.Serializable

data class PrimaryImage(
    val __typename: String,
    val caption: Caption,
    val height: Int,
    val id: String,
    var url: String,
    val width: Int,
    var filePath: String? = null
): Serializable