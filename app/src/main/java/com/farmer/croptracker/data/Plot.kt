package com.farmer.croptracker.data // IMPORTANT: Change "yourname.croptracker" to match your actual package!

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plots")
data class Plot(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val plotName: String,
    val cropName: String,
    val cropBreed: String,
    val imageUri: String? = null // Nullable because they might not add an image immediately
)
