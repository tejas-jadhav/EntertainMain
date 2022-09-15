package com.example.entertainmain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchList(
    @PrimaryKey
    val id: String,
    val movieTitle: String,
    var watched: Boolean = false
)
