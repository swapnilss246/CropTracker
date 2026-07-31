package com.farmer.croptracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.farmer.croptracker.data.CropDatabase
import com.farmer.croptracker.ui.HomeScreen
import com.farmer.croptracker.ui.theme.CropTrackerTheme // This should match whatever the IDE generated
import com.farmer.croptracker.viewmodel.CropViewModel
import com.farmer.croptracker.viewmodel.CropViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Build the database connection
        val database = CropDatabase.getDatabase(this)
        val factory = CropViewModelFactory(database.plotDao())

        setContent {
            CropTrackerTheme {
                // Generate the ViewModel using our factory
                val viewModel: CropViewModel = viewModel(factory = factory)
                
                // Launch the Home Screen!
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}