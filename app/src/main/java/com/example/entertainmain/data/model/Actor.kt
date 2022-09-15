package com.example.entertainmain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "actors")
data class Actor(
    @PrimaryKey(autoGenerate = true)
    var roomId: Int? = null,
    val actorName: String,
    val imageUrl: String
): Serializable
