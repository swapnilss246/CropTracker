package com.yourname.croptracker.data // Change to your package

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val expenseId: Int = 0,
    val plotId: Int, // Links back to the Plot table
    val category: String, // Seed, Fertilizer, Labor, etc.
    val cost: Double,
    val dateMillis: Long // Storing date as a timestamp
)
