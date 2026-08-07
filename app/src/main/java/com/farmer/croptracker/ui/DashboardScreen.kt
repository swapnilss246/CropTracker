package com.farmer.croptracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farmer.croptracker.viewmodel.CropViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: CropViewModel, onNavigateBack: () -> Unit) {
    val activePlots by viewModel.activePlotCount.collectAsState(initial = 0)
    val totalExpenses by viewModel.totalActiveExpenses.collectAsState(initial = 0.0)
    val totalRevenue by viewModel.totalActiveRevenue.collectAsState(initial = 0.0)

    // Null safety fallbacks
    val expenses = totalExpenses ?: 0.0
    val revenue = totalRevenue ?: 0.0
    val profit = revenue - expenses

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global Farm Statistics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(), 
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active Plots Card
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Active Seasons (Plots)", style = MaterialTheme.typography.titleMedium)
                    Text("${activePlots ?: 0}", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
            
            // Expenses Card
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Active Expenses", style = MaterialTheme.typography.titleMedium)
                    Text("₹$expenses", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.error)
                }
            }

            // Revenue Card
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Estimated Revenue (Yields)", style = MaterialTheme.typography.titleMedium)
                    Text("₹$revenue", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                }
            }

            // Net Profit Card (Changes color based on profit/loss!)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (profit >= 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Estimated Net Profit", style = MaterialTheme.typography.titleMedium)
                    Text("₹$profit", style = MaterialTheme.typography.headlineLarge)
                }
            }
        }
    }
}