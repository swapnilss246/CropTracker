package com.farmer.croptracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.farmer.croptracker.data.CropDatabase
import com.farmer.croptracker.ui.HomeScreen
import com.farmer.croptracker.viewmodel.CropViewModel
import com.farmer.croptracker.viewmodel.CropViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Build the database connection
        val database = CropDatabase.getDatabase(this)
        val factory = CropViewModelFactory(database.plotDao())

        // 2. Generate the ViewModel the traditional Android way (no extra dependencies needed)
        val viewModel = ViewModelProvider(this, factory)[CropViewModel::class.java]

        setContent {
            // 3. Use the built-in MaterialTheme instead of the missing auto-generated one
            MaterialTheme {
                // Launch the Home Screen!
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}