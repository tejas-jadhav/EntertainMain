package com.example.entertainmain.data.model

import java.io.Serializable

data class RatingsSummary(
    val __typename: String,
    val aggregateRating: Double,
    val voteCount: Int
): Serializable