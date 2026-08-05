package com.farmer.croptracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "treatments")
data class Treatment(
    @PrimaryKey(autoGenerate = true) val treatmentId: Int = 0,
    val plotId: Int,
    val treatmentName: String, // e.g., Urea, Neem Oil
    val chemicalQuantity: String, // e.g., 500 ml, 2 kg
    val waterQuantity: String, // e.g., 200 Liters (can be blank if not applicable)
    val applicationMethod: String, // e.g., Foliar Spray, Drip
    val description: String, // Multi-line notes
    val dateMillis: Long,
    val imageUri: String? = null, // Supports images!
    val isDeleted: Boolean = false,
    val deletedAtMillis: Long = 0
)