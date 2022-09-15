package com.example.entertainmain.data.source.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.entertainmain.data.model.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(movie: Movie): Long

    @Query("SELECT * FROM movies")
    fun getAllFavoriteMovies(): LiveData<List<Movie>>

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("SELECT * FROM movies WHERE id=:id")
    fun getFavoriteMovieById(id: String) : LiveData<Movie>
}