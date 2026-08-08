package com.farmer.croptracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.farmer.croptracker.R
import com.farmer.croptracker.viewmodel.CropViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: CropViewModel, onNavigateBack: () -> Unit) {
    val activePlots by viewModel.activePlotCount.collectAsState(initial = 0)
    val totalExpenses by viewModel.totalActiveExpenses.collectAsState(initial = 0.0)
    val totalRevenue by viewModel.totalActiveRevenue.collectAsState(initial = 0.0)

    val expenses = totalExpenses ?: 0.0
    val revenue = totalRevenue ?: 0.0
    val profit = revenue - expenses

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.global_farm_stats)) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.active_seasons_plots), style = MaterialTheme.typography.titleMedium)
                    Text("${activePlots ?: 0}", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.total_active_expenses), style = MaterialTheme.typography.titleMedium)
                    Text("₹$expenses", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.error)
                }
            }
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.estimated_revenue_yields), style = MaterialTheme.typography.titleMedium)
                    Text("₹$revenue", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = if (profit >= 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.estimated_net_profit), style = MaterialTheme.typography.titleMedium)
                    Text("₹$profit", style = MaterialTheme.typography.headlineLarge)
                }
            }
        }
    }
}