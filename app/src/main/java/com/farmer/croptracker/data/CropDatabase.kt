package com.farmer.croptracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// NEW: Added Treatment::class to the entities list!
@Database(entities = [Plot::class, Expense::class, CropYield::class, Treatment::class], version = 1, exportSchema = false)
abstract class CropDatabase : RoomDatabase() {
    
    abstract fun plotDao(): PlotDao

    companion object {
        @Volatile
        private var Instance: CropDatabase? = null

        fun getDatabase(context: Context): CropDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CropDatabase::class.java, "crop_database")
                    .fallbackToDestructiveMigration() 
                    .build()
                    .also { Instance = it }
            }
        }
    }
}