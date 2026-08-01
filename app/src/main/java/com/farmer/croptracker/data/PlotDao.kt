package com.farmer.croptracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlotDao {
    @Query("SELECT * FROM plots ORDER BY id DESC")
    fun getAllPlots(): Flow<List<Plot>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlot(plot: Plot)

    // --- NEW: Load and Save Expenses ---
    @Query("SELECT * FROM expenses WHERE plotId = :plotId ORDER BY dateMillis DESC")
    fun getExpensesForPlot(plotId: Int): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpense(expense: Expense)

    // --- NEW: Load and Save Yields ---
    @Query("SELECT * FROM yields WHERE plotId = :plotId ORDER BY dateMillis DESC")
    fun getYieldsForPlot(plotId: Int): Flow<List<CropYield>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertYield(yield: CropYield)

    // --- NEW: Delete Plot ---
    @Query("DELETE FROM plots WHERE id = :plotId")
    suspend fun deletePlotById(plotId: Int)
}