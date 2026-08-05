package com.farmer.croptracker.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) 
    val expenseId: Int = 0,
    val plotId: Int, // Links back to the Plot table
    val category: String, // Seed, Fertilizer, Labor, etc.
    val cost: Double,
    val dateMillis: Long, // Storing date as a timestamp
    val isDeleted: Boolean = false, // NEW: Soft delete flag
	val deletedAtMillis: Long = 0 // NEW
)