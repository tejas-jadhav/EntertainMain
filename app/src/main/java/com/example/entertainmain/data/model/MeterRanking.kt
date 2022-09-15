package com.example.entertainmain.data.model

import java.io.Serializable

data class MeterRanking(
    val __typename: String,
    val currentRank: Int,
    val rankChange: RankChange
): Serializable