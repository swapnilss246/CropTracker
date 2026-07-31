package com.farmer.croptracker.ui // Change to your package if needed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farmer.croptracker.viewmodel.CropViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: CropViewModel) {
    // 1. We 'collect' the live data from the database. 
    // Whenever a plot is added, this list updates automatically!
    val plotList by viewModel.allPlots.collectAsState()

    // 2. This remembers whether the "Add Plot" popup should be open or closed
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
        
        // 3. This is our scrollable list (RecyclerView replacement)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(plotList) { plot ->
                // This draws a beautiful card for every plot in the database
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = plot.plotName, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Crop: ${plot.cropName}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Breed: ${plot.cropBreed}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

    // 4. The Pop-Up Dialog to add a new plot
    if (showDialog) {
        var plotName by remember { mutableStateOf("") }
        var cropName by remember { mutableStateOf("") }
        var cropBreed by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Plot") },
            text = {
                Column {
                    OutlinedTextField(
                        value = plotName,
                        onValueChange = { plotName = it },
                        label = { Text("Plot Name (e.g. North Field)") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = cropName,
                        onValueChange = { cropName = it },
                        label = { Text("Crop (e.g. Wheat)") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = cropBreed,
                        onValueChange = { cropBreed = it },
                        label = { Text("Breed/Variety") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (plotName.isNotBlank() && cropName.isNotBlank()) {
                        // Send the data to the ViewModel to save in the database
                        viewModel.addPlot(plotName, cropName, cropBreed)
                        showDialog = false
                    }
                }) {
                    Text("Save Plot")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}