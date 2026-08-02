package com.farmer.croptracker.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "yields")
data class CropYield(
    @PrimaryKey(autoGenerate = true) 
    val yieldId: Int = 0,
    val plotId: Int,
    val quantity: Double,
    val unit: String,
    val marketRate: Double,
    val dateMillis: Long,
    val isDeleted: Boolean = false // NEW: Soft delete flag
)