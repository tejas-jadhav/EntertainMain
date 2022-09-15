package com.example.entertainmain.data.source.db

import androidx.room.TypeConverter
import com.example.entertainmain.data.model.*
import okhttp3.internal.toImmutableList
import java.util.*

class MovieConverters {

    @TypeConverter
    fun fromGenres(genres: Genres): String {
        var result = ""
        for (i in 0 until genres.genres.size) {
            result += genres.genres[i].id
            if (i != genres.genres.size - 1) {
                result += ","
            }
        }
        return result
    }

    @TypeConverter
    fun toGenres(string: String): Genres {
        val listOfGenresString = string.split(",")
        val tempListOfGenre = mutableListOf<Genre>()
        for (genreString in listOfGenresString) {
            tempListOfGenre.add(
                Genre(
                    "Genre",
                    genreString,
                    genreString
                )
            )
        }
        return Genres("Genres", tempListOfGenre.toImmutableList())
    }

    @TypeConverter
    fun fromMeterRanking(meterRanking: MeterRanking): Int {
        return meterRanking.currentRank
    }

    @TypeConverter
    fun toMeterRanking(currentRank: Int): MeterRanking {
        return MeterRanking(
            "MeterRanking", currentRank,
            RankChange(
                "RankChange",
                "",
                0
            )
        )
    }

    @TypeConverter
    fun fromPlot(plot: Plot): String {
        return plot.plotText.plainText
    }

    @TypeConverter
    fun toPlot(plainText: String): Plot {
        return Plot(
            "Plot",
            Language(
                "Language",
                "English"
            ),
            PlotText(
                "PlotText",
                plainText
            )
        )
    }

    @TypeConverter
    fun fromPrimaryImage(primaryImage: PrimaryImage): String {
        return primaryImage.url
    }

    @TypeConverter
    fun toPrimaryImage(url: String): PrimaryImage {
        return PrimaryImage(
            "PrimaryImage",
            Caption("Caption", ""),
            0,
            "",
            url,
            0
        )
    }

    @TypeConverter
    fun fromRatingSummary(ratingsSummary: RatingsSummary): Double {
        return ratingsSummary.aggregateRating
    }

    @TypeConverter
    fun toRatingSummary(aggregateRating: Double): RatingsSummary {
        return RatingsSummary(
            "RatingsSummary",
            aggregateRating,
            0
        )
    }

    @TypeConverter
    fun fromReleaseDate(releaseDate: ReleaseDate): String {
        return "${releaseDate.day}/${releaseDate.month}/${releaseDate.year}"
    }

    @TypeConverter
    fun toReleaseDate(dateString: String): ReleaseDate {
        val date = dateString.split("/")
        return ReleaseDate(
            "ReleaseDate",
            date[0].toInt(),
            date[1].toInt(),
            date[2].toInt()
        )
    }


    @TypeConverter
    fun fromReleaseYear(releaseYear: ReleaseYear): Int {
        return releaseYear.year
    }

    @TypeConverter
    fun toReleaseYear(year: Int): ReleaseYear {
        return ReleaseYear(
            "ReleaseYear",
            Any(),
            year
        )
    }

    @TypeConverter
    fun fromRuntime(runtime: Runtime): Int {
        return runtime.seconds
    }

    @TypeConverter
    fun toRuntime(seconds: Int): Runtime {
        return Runtime("Runtime", seconds)
    }

    @TypeConverter
    fun fromTitleText(titleText: TitleText): String {
        return titleText.text
    }

    @TypeConverter
    fun toTitleText(title: String): TitleText {
        return TitleText(
            "TitleText",
            title
        )
    }

    @TypeConverter
    fun fromTitleType(titleType: TitleType): String {
        return titleType.id
    }

    @TypeConverter
    fun toTitleType(type: String): TitleType {
        return TitleType(
            "TitleType",
            type,
            isEpisode = false, isSeries = false,
            text = type
        )
    }




}