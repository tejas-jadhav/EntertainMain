package com.example.entertainmain.data.model

import java.io.Serializable

data class RankChange(
    val __typename: String,
    val changeDirection: String,
    val difference: Int
): Serializable