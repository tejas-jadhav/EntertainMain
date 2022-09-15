package com.example.entertainmain.data.source.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.entertainmain.data.model.WatchList

@Dao
interface WatchListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWatchList(watchList: WatchList): Long

    @Delete
    suspend fun deleteWatchList(watchList: WatchList)

    @Query("SELECT * FROM watchlist WHERE watched = 1")
    fun getWatchListWatched(): LiveData<List<WatchList>>

    @Query("SELECT * FROM watchlist WHERE watched = 0")
    fun getWatchListUnwatched(): LiveData<List<WatchList>>

}