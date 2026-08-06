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
    // NEW: Now only shows plots that are NOT deleted AND NOT archived
    @Query("SELECT * FROM plots WHERE isDeleted = 0 AND isArchived = 0 ORDER BY id DESC")
    fun getAllPlots(): Flow<List<Plot>>

    @Query("SELECT * FROM plots WHERE isDeleted = 1 ORDER BY deletedAtMillis DESC")
    fun getDeletedPlots(): Flow<List<Plot>>

    // NEW: Fetch archived plots for the future dashboard
    @Query("SELECT * FROM plots WHERE isDeleted = 0 AND isArchived = 1 ORDER BY id DESC")
    fun getArchivedPlots(): Flow<List<Plot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlot(plot: Plot)

    // NEW: Archiving commands
    @Query("UPDATE plots SET isArchived = 1 WHERE id = :id")
    suspend fun archivePlot(id: Int)

    @Query("UPDATE plots SET isArchived = 0 WHERE id = :id")
    suspend fun unarchivePlot(id: Int)

    @Query("UPDATE plots SET isDeleted = 1, deletedAtMillis = :timestamp WHERE id = :id")
    suspend fun softDeletePlot(id: Int, timestamp: Long)

    @Query("UPDATE plots SET isDeleted = 0, deletedAtMillis = 0 WHERE id = :id")
    suspend fun restorePlot(id: Int)

    @Delete
    suspend fun permanentlyDeletePlot(plot: Plot)

    // --- EXPENSES ---
    @Query("SELECT * FROM expenses WHERE plotId = :plotId AND isDeleted = 0 ORDER BY dateMillis DESC")
    fun getExpensesForPlot(plotId: Int): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE plotId = :plotId AND isDeleted = 1 ORDER BY deletedAtMillis DESC")
    fun getDeletedExpensesForPlot(plotId: Int): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("UPDATE expenses SET isDeleted = 1, deletedAtMillis = :timestamp WHERE expenseId = :id")
    suspend fun softDeleteExpense(id: Int, timestamp: Long)

    @Query("UPDATE expenses SET isDeleted = 0, deletedAtMillis = 0 WHERE expenseId = :id")
    suspend fun restoreExpense(id: Int)

    @Delete
    suspend fun permanentlyDeleteExpense(expense: Expense)

    // --- YIELDS ---
    @Query("SELECT * FROM yields WHERE plotId = :plotId AND isDeleted = 0 ORDER BY dateMillis DESC")
    fun getYieldsForPlot(plotId: Int): Flow<List<CropYield>>

    @Query("SELECT * FROM yields WHERE plotId = :plotId AND isDeleted = 1 ORDER BY deletedAtMillis DESC")
    fun getDeletedYieldsForPlot(plotId: Int): Flow<List<CropYield>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYield(yield: CropYield)

    @Query("UPDATE yields SET isDeleted = 1, deletedAtMillis = :timestamp WHERE yieldId = :id")
    suspend fun softDeleteYield(id: Int, timestamp: Long)

    @Query("UPDATE yields SET isDeleted = 0, deletedAtMillis = 0 WHERE yieldId = :id")
    suspend fun restoreYield(id: Int)

    @Delete
    suspend fun permanentlyDeleteYield(yield: CropYield)
	
	// --- TREATMENTS ---
    @Query("SELECT * FROM treatments WHERE plotId = :plotId AND isDeleted = 0 ORDER BY dateMillis DESC")
    fun getTreatmentsForPlot(plotId: Int): Flow<List<Treatment>>

    @Query("SELECT * FROM treatments WHERE plotId = :plotId AND isDeleted = 1 ORDER BY deletedAtMillis DESC")
    fun getDeletedTreatmentsForPlot(plotId: Int): Flow<List<Treatment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreatment(treatment: Treatment)

    @Query("UPDATE treatments SET isDeleted = 1, deletedAtMillis = :timestamp WHERE treatmentId = :id")
    suspend fun softDeleteTreatment(id: Int, timestamp: Long)

    @Query("UPDATE treatments SET isDeleted = 0, deletedAtMillis = 0 WHERE treatmentId = :id")
    suspend fun restoreTreatment(id: Int)

    @Delete
    suspend fun permanentlyDeleteTreatment(treatment: Treatment)
}