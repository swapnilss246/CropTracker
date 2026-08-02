package com.farmer.croptracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlotDao {
    // --- PLOTS ---
    @Query("SELECT * FROM plots WHERE isDeleted = 0 ORDER BY id DESC")
    fun getAllPlots(): Flow<List<Plot>>

    @Query("SELECT * FROM plots WHERE isDeleted = 1 ORDER BY id DESC")
    fun getDeletedPlots(): Flow<List<Plot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlot(plot: Plot)

    @Query("UPDATE plots SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeletePlot(id: Int)

    @Query("UPDATE plots SET isDeleted = 0 WHERE id = :id")
    suspend fun restorePlot(id: Int)

    @Delete
    suspend fun permanentlyDeletePlot(plot: Plot)

    // --- EXPENSES ---
    @Query("SELECT * FROM expenses WHERE plotId = :plotId AND isDeleted = 0 ORDER BY dateMillis DESC")
    fun getExpensesForPlot(plotId: Int): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 1 ORDER BY dateMillis DESC")
    fun getDeletedExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("UPDATE expenses SET isDeleted = 1 WHERE expenseId = :id")
    suspend fun softDeleteExpense(id: Int)

    @Query("UPDATE expenses SET isDeleted = 0 WHERE expenseId = :id")
    suspend fun restoreExpense(id: Int)

    @Delete
    suspend fun permanentlyDeleteExpense(expense: Expense)

    // --- YIELDS ---
    @Query("SELECT * FROM yields WHERE plotId = :plotId AND isDeleted = 0 ORDER BY dateMillis DESC")
    fun getYieldsForPlot(plotId: Int): Flow<List<CropYield>>

    @Query("SELECT * FROM yields WHERE isDeleted = 1 ORDER BY dateMillis DESC")
    fun getDeletedYields(): Flow<List<CropYield>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYield(yield: CropYield)

    @Query("UPDATE yields SET isDeleted = 1 WHERE yieldId = :id")
    suspend fun softDeleteYield(id: Int)

    @Query("UPDATE yields SET isDeleted = 0 WHERE yieldId = :id")
    suspend fun restoreYield(id: Int)

    @Delete
    suspend fun permanentlyDeleteYield(yield: CropYield)
}