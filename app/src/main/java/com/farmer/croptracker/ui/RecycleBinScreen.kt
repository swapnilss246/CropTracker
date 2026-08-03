package com.farmer.croptracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farmer.croptracker.viewmodel.CropViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinScreen(
    viewModel: CropViewModel,
    onNavigateBack: () -> Unit
) {
    val deletedPlots by viewModel.deletedPlots.collectAsState(initial = emptyList())
    val deletedExpenses by viewModel.deletedExpenses.collectAsState(initial = emptyList())
    val deletedYields by viewModel.deletedYields.collectAsState(initial = emptyList())

    val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recycle Bin") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            )
        }
    ) { innerPadding ->
        
        if (deletedPlots.isEmpty() && deletedExpenses.isEmpty() && deletedYields.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Recycle Bin is empty", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // --- PLOTS ---
                if (deletedPlots.isNotEmpty()) {
                    item { Text("Deleted Plots", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary) }
                    items(deletedPlots) { plot ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(plot.plotName, style = MaterialTheme.typography.titleMedium)
                                    Text("Crop: ${plot.cropName}", style = MaterialTheme.typography.bodyMedium)
                                }
                                Row {
                                    IconButton(onClick = { viewModel.restorePlot(plot) }) { Icon(Icons.Filled.Refresh, "Restore", tint = MaterialTheme.colorScheme.primary) }
                                    IconButton(onClick = { viewModel.permanentlyDeletePlot(plot) }) { Icon(Icons.Filled.Delete, "Delete Permanently", tint = MaterialTheme.colorScheme.error) }
                                }
                            }
                        }
                    }
                }

                // --- EXPENSES ---
                if (deletedExpenses.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    item { Text("Deleted Expenses", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary) }
                    items(deletedExpenses) { exp ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${exp.category} - ₹${exp.cost}", style = MaterialTheme.typography.titleMedium)
                                    Text(formatter.format(Date(exp.dateMillis)), style = MaterialTheme.typography.bodyMedium)
                                }
                                Row {
                                    IconButton(onClick = { viewModel.restoreExpense(exp) }) { Icon(Icons.Filled.Refresh, "Restore", tint = MaterialTheme.colorScheme.primary) }
                                    IconButton(onClick = { viewModel.permanentlyDeleteExpense(exp) }) { Icon(Icons.Filled.Delete, "Delete Permanently", tint = MaterialTheme.colorScheme.error) }
                                }
                            }
                        }
                    }
                }

                // --- YIELDS ---
                if (deletedYields.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    item { Text("Deleted Yields", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary) }
                    items(deletedYields) { yld ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${yld.quantity} ${yld.unit}", style = MaterialTheme.typography.titleMedium)
                                    Text(formatter.format(Date(yld.dateMillis)), style = MaterialTheme.typography.bodyMedium)
                                }
                                Row {
                                    IconButton(onClick = { viewModel.restoreYield(yld) }) { Icon(Icons.Filled.Refresh, "Restore", tint = MaterialTheme.colorScheme.primary) }
                                    IconButton(onClick = { viewModel.permanentlyDeleteYield(yld) }) { Icon(Icons.Filled.Delete, "Delete Permanently", tint = MaterialTheme.colorScheme.error) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}