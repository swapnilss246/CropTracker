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
    val imageUri: String? = null,
    val isDeleted: Boolean = false, // NEW: Soft delete flag
	val deletedAtMillis: Long = 0, // NEW: to add the timestamp to the deleted item
    val isArchived: Boolean = false // NEW: For past seasons!
)