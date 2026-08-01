package com.farmer.croptracker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farmer.croptracker.viewmodel.CropViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Helper function to format the timestamp to 01-Jan-2026
fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CropViewModel,
    onPlotClick: (Int, String) -> Unit
) {
    val plotList by viewModel.allPlots.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Farm Plots") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Plot")
            }
        }
    ) { innerPadding ->
        
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(plotList) { plot ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().clickable { 
                        onPlotClick(plot.id, plot.plotName) 
                    },
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    // NEW: A Row to hold the text on the left, and Delete button on the right
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = plot.plotName, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Crop: ${plot.cropName}", style = MaterialTheme.typography.bodyLarge)
                            
                            // NEW: Row to hold breed and date side-by-side
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "Breed: ${plot.cropBreed}", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "•", style = MaterialTheme.typography.bodyMedium)
                                Text(text = formatDate(plot.createdAt), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        
                        // NEW: Delete Button
                        IconButton(onClick = { viewModel.deletePlot(plot.id) }) {
                            Icon(
                                imageVector = Icons.Filled.Delete, 
                                contentDescription = "Delete Plot",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var plotName by remember { mutableStateOf("") }
        var cropName by remember { mutableStateOf("") }
        var cropBreed by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Plot") },
            text = {
                Column {
                    OutlinedTextField(value = plotName, onValueChange = { plotName = it }, label = { Text("Plot Name") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = cropName, onValueChange = { cropName = it }, label = { Text("Crop") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = cropBreed, onValueChange = { cropBreed = it }, label = { Text("Breed/Variety") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (plotName.isNotBlank() && cropName.isNotBlank()) {
                        viewModel.addPlot(plotName, cropName, cropBreed)
                        showDialog = false
                    }
                }) { Text("Save Plot") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}