package com.farmer.croptracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plots")
data class Plot(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val plotName: String,
    val cropName: String,
    val cropBreed: String,
    val createdAt: Long = System.currentTimeMillis(), // NEW: Automatically saves the exact date it was created
    val imageUri: String? = null
)