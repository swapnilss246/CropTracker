package com.farmer.croptracker.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.farmer.croptracker.R
import com.farmer.croptracker.data.CropYield
import com.farmer.croptracker.data.Expense
import com.farmer.croptracker.data.Treatment
import com.farmer.croptracker.viewmodel.CropViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlotDetailScreen(
    plotId: Int,
    plotName: String,
    viewModel: CropViewModel,
    onNavigateBack: () -> Unit
) {
    var showRecycleBinMode by remember { mutableStateOf(false) }

    // Active Data
    val treatments by viewModel.getTreatments(plotId).collectAsState(initial = emptyList())
    val expenses by viewModel.getExpenses(plotId).collectAsState(initial = emptyList())
    val yields by viewModel.getYields(plotId).collectAsState(initial = emptyList())
    
    // Deleted Data
    val deletedTreatments by viewModel.getDeletedTreatments(plotId).collectAsState(initial = emptyList())
    val deletedExpenses by viewModel.getDeletedExpenses(plotId).collectAsState(initial = emptyList())
    val deletedYields by viewModel.getDeletedYields(plotId).collectAsState(initial = emptyList())

    // Plot Financials
    val plotExpensesFlow by viewModel.getPlotExpensesTotal(plotId).collectAsState(initial = 0.0)
    val plotRevenueFlow by viewModel.getPlotRevenueTotal(plotId).collectAsState(initial = 0.0)
    
    val pExpenses = plotExpensesFlow ?: 0.0
    val pRevenue = plotRevenueFlow ?: 0.0
    val pProfit = pRevenue - pExpenses

    // Dialog States
    var showTreatmentDialog by remember { mutableStateOf(false) }
    var treatmentBeingEdited by remember { mutableStateOf<Treatment?>(null) }
    
    var showExpenseDialog by remember { mutableStateOf(false) }
    var expenseBeingEdited by remember { mutableStateOf<Expense?>(null) }
    
    var showYieldDialog by remember { mutableStateOf(false) }
    var yieldBeingEdited by remember { mutableStateOf<CropYield?>(null) }

    val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showRecycleBinMode) "$plotName (Bin)" else plotName) },
                navigationIcon = {
                    IconButton(onClick = { if (showRecycleBinMode) showRecycleBinMode = false else onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!showRecycleBinMode) {
                        IconButton(onClick = { showRecycleBinMode = true }) { Icon(Icons.Filled.Delete, "Bin") }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (showRecycleBinMode) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            
            // --- MINI DASHBOARD SECTION ---
            if (!showRecycleBinMode) {
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (pProfit >= 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.expenses), style = MaterialTheme.typography.labelLarge)
                                Text("₹$pExpenses", style = MaterialTheme.typography.titleMedium)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.revenue), style = MaterialTheme.typography.labelLarge)
                                Text("₹$pRevenue", style = MaterialTheme.typography.titleMedium)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.profit), style = MaterialTheme.typography.labelLarge)
                                Text("₹$pProfit", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }

            // --- TREATMENTS SECTION ---
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (showRecycleBinMode) "Deleted Treatments" else stringResource(R.string.treatments), style = MaterialTheme.typography.titleLarge)
                    if (!showRecycleBinMode) Button(onClick = { treatmentBeingEdited = null; showTreatmentDialog = true }) { Text(stringResource(R.string.add)) }
                }
            }
            
            val treatmentsList = if (showRecycleBinMode) deletedTreatments else treatments
            items(treatmentsList) { trt ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        if (trt.imageUri != null) {
                            AsyncImage(model = trt.imageUri, contentDescription = "Image", modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(trt.treatmentName, style = MaterialTheme.typography.titleMedium)
                            Text("${trt.applicationMethod} • ${trt.chemicalQuantity}", style = MaterialTheme.typography.bodyMedium)
                            if (trt.description.isNotBlank()) Text(trt.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(formatter.format(Date(trt.dateMillis)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Row {
                            if (showRecycleBinMode) {
                                IconButton(onClick = { viewModel.restoreTreatment(trt) }) { Icon(Icons.Filled.Refresh, "Restore", tint = MaterialTheme.colorScheme.primary) }
                                IconButton(onClick = { viewModel.permanentlyDeleteTreatment(trt) }) { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                            } else {
                                IconButton(onClick = { treatmentBeingEdited = trt; showTreatmentDialog = true }) { Icon(Icons.Filled.Edit, "Edit") }
                                IconButton(onClick = { viewModel.deleteTreatment(trt) }) { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // --- EXPENSES SECTION ---
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (showRecycleBinMode) "Deleted Expenses" else stringResource(R.string.expenses), style = MaterialTheme.typography.titleLarge)
                    if (!showRecycleBinMode) Button(onClick = { expenseBeingEdited = null; showExpenseDialog = true }) { Text(stringResource(R.string.add)) }
                }
            }
            val expensesList = if (showRecycleBinMode) deletedExpenses else expenses
            items(expensesList) { exp ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(exp.category, style = MaterialTheme.typography.bodyLarge)
                            Text("₹${exp.cost}", style = MaterialTheme.typography.titleMedium)
                            Text(formatter.format(Date(exp.dateMillis)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Row {
                            if (showRecycleBinMode) {
                                IconButton(onClick = { viewModel.restoreExpense(exp) }) { Icon(Icons.Filled.Refresh, "Restore", tint = MaterialTheme.colorScheme.primary) }
                                IconButton(onClick = { viewModel.permanentlyDeleteExpense(exp) }) { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                            } else {
                                IconButton(onClick = { expenseBeingEdited = exp; showExpenseDialog = true }) { Icon(Icons.Filled.Edit, "Edit") }
                                IconButton(onClick = { viewModel.deleteExpense(exp) }) { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // --- YIELDS SECTION ---
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (showRecycleBinMode) "Deleted Yields" else stringResource(R.string.yields), style = MaterialTheme.typography.titleLarge)
                    if (!showRecycleBinMode) Button(onClick = { yieldBeingEdited = null; showYieldDialog = true }) { Text(stringResource(R.string.add)) }
                }
            }
            val yieldsList = if (showRecycleBinMode) deletedYields else yields
            items(yieldsList) { yld ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("${yld.quantity} ${yld.unit}", style = MaterialTheme.typography.titleMedium)
                            Text("Rate: ₹${yld.marketRate} / ${yld.unit}", style = MaterialTheme.typography.bodyMedium)
                            Text(formatter.format(Date(yld.dateMillis)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Row {
                            if (showRecycleBinMode) {
                                IconButton(onClick = { viewModel.restoreYield(yld) }) { Icon(Icons.Filled.Refresh, "Restore", tint = MaterialTheme.colorScheme.primary) }
                                IconButton(onClick = { viewModel.permanentlyDeleteYield(yld) }) { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                            } else {
                                IconButton(onClick = { yieldBeingEdited = yld; showYieldDialog = true }) { Icon(Icons.Filled.Edit, "Edit") }
                                IconButton(onClick = { viewModel.deleteYield(yld) }) { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- TREATMENT DIALOG ---
    if (showTreatmentDialog) {
        val context = LocalContext.current
        var tName by remember { mutableStateOf(treatmentBeingEdited?.treatmentName ?: "") }
        var chemQty by remember { mutableStateOf(treatmentBeingEdited?.chemicalQuantity ?: "") }
        var waterQty by remember { mutableStateOf(treatmentBeingEdited?.waterQuantity ?: "") }
        var method by remember { mutableStateOf(treatmentBeingEdited?.applicationMethod ?: "") }
        var desc by remember { mutableStateOf(treatmentBeingEdited?.description ?: "") }
        var dateMillis by remember { mutableStateOf(treatmentBeingEdited?.dateMillis ?: System.currentTimeMillis()) }
        var imageUri by remember { mutableStateOf(treatmentBeingEdited?.imageUri) }
        
        var showDatePicker by remember { mutableStateOf(false) }
        var expandedDropdown by remember { mutableStateOf(false) }

        val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val savedPath = copyImageToInternalStorage(context, uri)
                if (savedPath != null) imageUri = savedPath
            }
        }

        // NEW: Dynamic translated dropdown!
        val applicationMethods = listOf(
            stringResource(R.string.method_foliar) to stringResource(R.string.desc_foliar),
            stringResource(R.string.method_drip) to stringResource(R.string.desc_drip),
            stringResource(R.string.method_soil) to stringResource(R.string.desc_soil),
            stringResource(R.string.method_broadcasting) to stringResource(R.string.desc_broadcasting),
            stringResource(R.string.method_seed) to stringResource(R.string.desc_seed),
            stringResource(R.string.method_root) to stringResource(R.string.desc_root),
            stringResource(R.string.method_other) to stringResource(R.string.desc_other)
        )

        // Set default method dynamically
        if (method.isBlank()) method = stringResource(R.string.method_foliar)

        AlertDialog(
            onDismissRequest = { showTreatmentDialog = false },
            title = { Text(stringResource(R.string.treatments)) },
            text = {
                LazyColumn {
                    item {
                        if (imageUri != null) {
                            AsyncImage(model = imageUri, contentDescription = "Image", modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)).padding(bottom = 8.dp), contentScale = ContentScale.Crop)
                        }
                        OutlinedButton(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Text("Photo")
                        }

                        OutlinedTextField(value = tName, onValueChange = { tName = it }, label = { Text(stringResource(R.string.treatment_name)) }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                        OutlinedTextField(value = chemQty, onValueChange = { chemQty = it }, label = { Text(stringResource(R.string.chemical_qty)) }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                        OutlinedTextField(value = waterQty, onValueChange = { waterQty = it }, label = { Text(stringResource(R.string.water_qty)) }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                        
                        ExposedDropdownMenuBox(expanded = expandedDropdown, onExpandedChange = { expandedDropdown = !expandedDropdown }) {
                            OutlinedTextField(
                                value = method, onValueChange = {}, readOnly = true,
                                label = { Text(stringResource(R.string.application_method)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                                modifier = Modifier.menuAnchor().fillMaxWidth().padding(bottom = 8.dp)
                            )
                            ExposedDropdownMenu(expanded = expandedDropdown, onDismissRequest = { expandedDropdown = false }) {
                                applicationMethods.forEach { (methodName, detail) ->
                                    DropdownMenuItem(
                                        text = { Column { Text(methodName, style = MaterialTheme.typography.bodyLarge); Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                                        onClick = { method = methodName; expandedDropdown = false }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text(stringResource(R.string.description)) }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), minLines = 3, maxLines = 5)
                        OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) { Text("${stringResource(R.string.date)}: ${formatter.format(Date(dateMillis))}") }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (tName.isNotBlank() && chemQty.isNotBlank()) {
                        viewModel.addOrUpdateTreatment(Treatment(treatmentId = treatmentBeingEdited?.treatmentId ?: 0, plotId = plotId, treatmentName = tName, chemicalQuantity = chemQty, waterQuantity = waterQty, applicationMethod = method, description = desc, dateMillis = dateMillis, imageUri = imageUri))
                        showTreatmentDialog = false
                    }
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = { TextButton(onClick = { showTreatmentDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )

        if (showDatePicker) {
            val dateState = rememberDatePickerState(initialSelectedDateMillis = dateMillis, selectableDates = object : SelectableDates { override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= System.currentTimeMillis() })
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { dateState.selectedDateMillis?.let { dateMillis = it }; showDatePicker = false }) { Text("OK") } }) { DatePicker(state = dateState) }
        }
    }

    // --- EXPENSE DIALOG ---
    if (showExpenseDialog) {
        var category by remember { mutableStateOf(expenseBeingEdited?.category ?: "") }
        var cost by remember { mutableStateOf(expenseBeingEdited?.cost?.toString() ?: "") }
        var dateMillis by remember { mutableStateOf(expenseBeingEdited?.dateMillis ?: System.currentTimeMillis()) }
        var showDatePicker by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showExpenseDialog = false },
            title = { Text(stringResource(R.string.expenses)) },
            text = {
                Column {
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text(stringResource(R.string.category)) }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = cost, onValueChange = { if (!it.contains("-")) cost = it }, label = { Text(stringResource(R.string.cost)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) { Text("${stringResource(R.string.date)}: ${formatter.format(Date(dateMillis))}") }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val costDouble = cost.toDoubleOrNull()
                    if (category.isNotBlank() && costDouble != null && costDouble >= 0) {
                        viewModel.addOrUpdateExpense(Expense(expenseId = expenseBeingEdited?.expenseId ?: 0, plotId = plotId, category = category, cost = costDouble, dateMillis = dateMillis))
                        showExpenseDialog = false
                    }
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = { TextButton(onClick = { showExpenseDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
        if (showDatePicker) {
            val dateState = rememberDatePickerState(initialSelectedDateMillis = dateMillis, selectableDates = object : SelectableDates { override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= System.currentTimeMillis() })
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { dateState.selectedDateMillis?.let { dateMillis = it }; showDatePicker = false }) { Text("OK") } }) { DatePicker(state = dateState) }
        }
    }

    // --- YIELD DIALOG ---
    if (showYieldDialog) {
        var quantity by remember { mutableStateOf(yieldBeingEdited?.quantity?.toString() ?: "") }
        var unit by remember { mutableStateOf(yieldBeingEdited?.unit ?: "kg") }
        var rate by remember { mutableStateOf(yieldBeingEdited?.marketRate?.toString() ?: "") }
        var dateMillis by remember { mutableStateOf(yieldBeingEdited?.dateMillis ?: System.currentTimeMillis()) }
        var showDatePicker by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showYieldDialog = false },
            title = { Text(stringResource(R.string.yields)) },
            text = {
                Column {
                    OutlinedTextField(value = quantity, onValueChange = { if (!it.contains("-")) quantity = it }, label = { Text(stringResource(R.string.quantity)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text(stringResource(R.string.unit)) }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = rate, onValueChange = { if (!it.contains("-")) rate = it }, label = { Text(stringResource(R.string.rate_per_unit)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) { Text("${stringResource(R.string.date)}: ${formatter.format(Date(dateMillis))}") }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val qDouble = quantity.toDoubleOrNull()
                    val rDouble = rate.toDoubleOrNull()
                    if (qDouble != null && rDouble != null && qDouble >= 0 && rDouble >= 0) {
                        viewModel.addOrUpdateYield(CropYield(yieldId = yieldBeingEdited?.yieldId ?: 0, plotId = plotId, quantity = qDouble, unit = unit, marketRate = rDouble, dateMillis = dateMillis))
                        showYieldDialog = false
                    }
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = { TextButton(onClick = { showYieldDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
        if (showDatePicker) {
            val dateState = rememberDatePickerState(initialSelectedDateMillis = dateMillis, selectableDates = object : SelectableDates { override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= System.currentTimeMillis() })
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { dateState.selectedDateMillis?.let { dateMillis = it }; showDatePicker = false }) { Text("OK") } }) { DatePicker(state = dateState) }
        }
    }
}