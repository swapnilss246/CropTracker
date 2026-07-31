package com.farmer.croptracker.data // Remember to change to your package!

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlotDao {
    // This loads all plots. 'Flow' means it will automatically update the UI 
    // instantly whenever a new plot is added!
    @Query("SELECT * FROM plots ORDER BY id DESC")
    fun getAllPlots(): Flow<List<Plot>>

    // This saves a new plot to the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlot(plot: Plot)
}
