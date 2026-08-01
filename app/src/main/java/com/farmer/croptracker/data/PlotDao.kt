package com.farmer.croptracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlotDao {
    // --- PLOTS ---
    @Query("SELECT * FROM plots ORDER BY id DESC")
    fun getAllPlots(): Flow<List<Plot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlot(plot: Plot)

    @Update
    suspend fun updatePlot(plot: Plot)

    @Delete
    suspend fun deletePlot(plot: Plot)

    // --- EXPENSES ---
    @Query("SELECT * FROM expenses WHERE plotId = :plotId ORDER BY dateMillis DESC")
    fun getExpensesForPlot(plotId: Int): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // --- YIELDS ---
    @Query("SELECT * FROM yields WHERE plotId = :plotId ORDER BY dateMillis DESC")
    fun getYieldsForPlot(plotId: Int): Flow<List<CropYield>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYield(yield: CropYield)

    @Update
    suspend fun updateYield(yield: CropYield)

    @Delete
    suspend fun deleteYield(yield: CropYield)
}