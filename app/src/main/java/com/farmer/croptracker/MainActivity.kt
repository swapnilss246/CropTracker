package com.farmer.croptracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.farmer.croptracker.data.CropDatabase
import com.farmer.croptracker.ui.HomeScreen
import com.farmer.croptracker.ui.PlotDetailScreen
import com.farmer.croptracker.ui.RecycleBinScreen
import com.farmer.croptracker.viewmodel.CropViewModel
import com.farmer.croptracker.viewmodel.CropViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = CropDatabase.getDatabase(this)
        val factory = CropViewModelFactory(database.plotDao())
        val viewModel = ViewModelProvider(this, factory)[CropViewModel::class.java]

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onPlotClick = { plotId, plotName -> navController.navigate("details/$plotId/$plotName") },
                            onNavigateToRecycleBin = { navController.navigate("recycle_bin") },
                            onNavigateToHistory = { navController.navigate("historical_plots") } // NEW
                        )
                    }

                    composable(
                        route = "details/{plotId}/{plotName}",
                        arguments = listOf(
                            navArgument("plotId") { type = NavType.IntType },
                            navArgument("plotName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val plotId = backStackEntry.arguments?.getInt("plotId") ?: 0
                        val plotName = backStackEntry.arguments?.getString("plotName") ?: "Details"
                        
                        PlotDetailScreen(
                            plotId = plotId,
                            plotName = plotName,
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("recycle_bin") {
                        RecycleBinScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
                    }

                    // NEW ROUTE: Historical Plots
                    composable("historical_plots") {
                        HistoricalPlotsScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}