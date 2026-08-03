package com.farmer.croptracker.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.farmer.croptracker.data.Plot
import com.farmer.croptracker.viewmodel.CropViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

// NEW: Copies the selected gallery image into the app's secure internal storage
fun copyImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "plot_img_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CropViewModel,
    onPlotClick: (Int, String) -> Unit,
    onNavigateToRecycleBin: () -> Unit // NEW: Router command
) {
    val plotList by viewModel.allPlots.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var plotBeingEdited by remember { mutableStateOf<Plot?>(null) }
    
    // We can remove the Snackbar variables since we have a real Recycle Bin now!
    // (If you want to keep the quick Undo, you can, but it is no longer required)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Farm Plots") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    // NEW: Recycle Bin Button in the top right corner
                    IconButton(onClick = onNavigateToRecycleBin) {
                        Icon(Icons.Filled.Delete, contentDescription = "Recycle Bin")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                plotBeingEdited = null 
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
                        // NEW: Show the image thumbnail if it exists
                        if (plot.imageUri != null) {
                            AsyncImage(
                                model = plot.imageUri,
                                contentDescription = "Plot Image",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                        }

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
                            }) { Icon(Icons.Filled.Edit, contentDescription = "Edit Plot") }
                            
                            IconButton(onClick = { 
                                viewModel.deletePlot(plot)
                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(message = "Plot deleted", actionLabel = "UNDO")
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.addOrUpdatePlot(plot) 
                                    }
                                }
                            }) { Icon(Icons.Filled.Delete, contentDescription = "Delete Plot", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        val context = LocalContext.current
        var plotName by remember { mutableStateOf(plotBeingEdited?.plotName ?: "") }
        var cropName by remember { mutableStateOf(plotBeingEdited?.cropName ?: "") }
        var cropBreed by remember { mutableStateOf(plotBeingEdited?.cropBreed ?: "") }
        var selectedDateMillis by remember { mutableStateOf(plotBeingEdited?.createdAt ?: System.currentTimeMillis()) }
        var imageUri by remember { mutableStateOf(plotBeingEdited?.imageUri) } // Holds our image path
        
        var showDatePicker by remember { mutableStateOf(false) }

        // NEW: This launcher opens the phone's image gallery
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                // Copy the selected image to internal storage so it is never lost
                val savedPath = copyImageToInternalStorage(context, uri)
                if (savedPath != null) {
                    imageUri = savedPath
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (plotBeingEdited == null) "Add New Plot" else "Edit Plot") },
            text = {
                Column {
                    // NEW: Show the selected image inside the dialog
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .padding(bottom = 8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Text(if (imageUri == null) "Add Photo" else "Change Photo")
                    }

                    OutlinedTextField(value = plotName, onValueChange = { plotName = it }, label = { Text("Plot Name") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = cropName, onValueChange = { cropName = it }, label = { Text("Crop") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = cropBreed, onValueChange = { cropBreed = it }, label = { Text("Breed/Variety") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Date: ${formatDate(selectedDateMillis)}")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (plotName.isNotBlank() && cropName.isNotBlank()) {
                        val finalPlot = Plot(
                            id = plotBeingEdited?.id ?: 0,
                            plotName = plotName,
                            cropName = cropName,
                            cropBreed = cropBreed,
                            createdAt = selectedDateMillis,
                            imageUri = imageUri // Save the image path to the database!
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

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDateMillis,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis <= System.currentTimeMillis()
                    }
                }
            )
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