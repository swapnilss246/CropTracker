package com.farmer.croptracker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farmer.croptracker.data.Plot
import com.farmer.croptracker.viewmodel.CropViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    
    // UI States
    var showDialog by remember { mutableStateOf(false) }
    var plotBeingEdited by remember { mutableStateOf<Plot?>(null) }
    
    // Snackbar for Undo
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            FloatingActionButton(onClick = { 
                plotBeingEdited = null // New Plot
                showDialog = true 
            }) {
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
                    modifier = Modifier.fillMaxWidth().clickable { onPlotClick(plot.id, plot.plotName) },
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = plot.plotName, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Crop: ${plot.cropName}", style = MaterialTheme.typography.bodyLarge)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "Breed: ${plot.cropBreed}", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "•", style = MaterialTheme.typography.bodyMedium)
                                Text(text = formatDate(plot.createdAt), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Row {
                            IconButton(onClick = { 
                                plotBeingEdited = plot
                                showDialog = true 
                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Plot")
                            }
                            IconButton(onClick = { 
                                viewModel.deletePlot(plot)
                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(message = "Plot deleted", actionLabel = "UNDO")
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.addOrUpdatePlot(plot) // Restores the exact plot
                                    }
                                }
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Plot", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var plotName by remember { mutableStateOf(plotBeingEdited?.plotName ?: "") }
        var cropName by remember { mutableStateOf(plotBeingEdited?.cropName ?: "") }
        var cropBreed by remember { mutableStateOf(plotBeingEdited?.cropBreed ?: "") }
        var selectedDateMillis by remember { mutableStateOf(plotBeingEdited?.createdAt ?: System.currentTimeMillis()) }
        
        var showDatePicker by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (plotBeingEdited == null) "Add New Plot" else "Edit Plot") },
            text = {
                Column {
                    OutlinedTextField(value = plotName, onValueChange = { plotName = it }, label = { Text("Plot Name") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = cropName, onValueChange = { cropName = it }, label = { Text("Crop") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = cropBreed, onValueChange = { cropBreed = it }, label = { Text("Breed/Variety") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    
                    // Date Button
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Date: ${formatDate(selectedDateMillis)}")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (plotName.isNotBlank() && cropName.isNotBlank()) {
                        val finalPlot = Plot(
                            id = plotBeingEdited?.id ?: 0, // Keeps the same ID if editing
                            plotName = plotName,
                            cropName = cropName,
                            cropBreed = cropBreed,
                            createdAt = selectedDateMillis,
                            imageUri = plotBeingEdited?.imageUri
                        )
                        viewModel.addOrUpdatePlot(finalPlot)
                        showDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )

        // The Date Picker Popup
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDateMillis = it }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}