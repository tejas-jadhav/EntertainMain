package com.example.entertainmain.data.model

import java.io.Serializable

data class TitleType(
    val __typename: String,
    val id: String,
    val isEpisode: Boolean,
    val isSeries: Boolean,
    val text: String
): Serializable