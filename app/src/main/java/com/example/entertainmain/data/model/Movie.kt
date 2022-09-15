package com.example.entertainmain.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    val id: String,
    val titleType: TitleType,
    val titleText: TitleText,
    val primaryImage: PrimaryImage?,
    val ratingsSummary: RatingsSummary?,
    val genres: Genres?,
    val plot: Plot?,
    val meterRanking: MeterRanking?,
    val position: Int?,
    val releaseDate: ReleaseDate?,
    val releaseYear: ReleaseYear?,
    val runtime: Runtime?,
    var savedImageFilePath: String? = null
): Serializable