package com.farmer.croptracker.data // Remember to change to your package!

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// We list all 3 tables here so the database knows about them
@Database(entities = [Plot::class, Expense::class, CropYield::class], version = 1, exportSchema = false)
abstract class CropDatabase : RoomDatabase() {
    
    abstract fun plotDao(): PlotDao

    companion object {
        @Volatile
        private var Instance: CropDatabase? = null

        fun getDatabase(context: Context): CropDatabase {
            // If the database already exists, return it. Otherwise, build it.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    CropDatabase::class.java,
                    "crop_database"
                )
                .build()
                .also { Instance = it }
            }
        }
    }
}
