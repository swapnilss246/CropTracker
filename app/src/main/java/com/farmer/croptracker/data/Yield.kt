package com.famrmer.croptracker.data // Change to your package

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "yields")
data class Yield(
    @PrimaryKey(autoGenerate = true)
    val yieldId: Int = 0,
    val plotId: Int, // Links back to the Plot table
    val quantity: Double,
    val unit: String, // kg, ton, box
    val marketRate: Double,
    val dateMillis: Long
)
