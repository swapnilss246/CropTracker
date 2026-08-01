package com.farmer.croptracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.farmer.croptracker.data.CropYield
import com.farmer.croptracker.data.Expense
import com.farmer.croptracker.viewmodel.CropViewModel
import kotlinx.coroutines.launch
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
    val expenses by viewModel.getExpenses(plotId).collectAsState(initial = emptyList())
    val yields by viewModel.getYields(plotId).collectAsState(initial = emptyList())

    var showExpenseDialog by remember { mutableStateOf(false) }
    var expenseBeingEdited by remember { mutableStateOf<Expense?>(null) }
    
    var showYieldDialog by remember { mutableStateOf(false) }
    var yieldBeingEdited by remember { mutableStateOf<CropYield?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(plotName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // --- EXPENSES SECTION ---
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Expenses", style = MaterialTheme.typography.titleLarge)
                    Button(onClick = { expenseBeingEdited = null; showExpenseDialog = true }) { Text("Add Expense") }
                }
            }
            items(expenses) { exp ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(exp.category, style = MaterialTheme.typography.bodyLarge)
                            Text("₹${exp.cost}", style = MaterialTheme.typography.titleMedium)
                            
                            val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                            Text(formatter.format(Date(exp.dateMillis)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Row {
                            IconButton(onClick = { expenseBeingEdited = exp; showExpenseDialog = true }) { Icon(Icons.Filled.Edit, "Edit") }
                            IconButton(onClick = { 
                                viewModel.deleteExpense(exp)
                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar("Expense deleted", "UNDO")
                                    if (result == SnackbarResult.ActionPerformed) viewModel.addOrUpdateExpense(exp)
                                }
                            }) { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // --- YIELDS SECTION ---
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Yields & Harvests", style = MaterialTheme.typography.titleLarge)
                    Button(onClick = { yieldBeingEdited = null; showYieldDialog = true }) { Text("Add Yield") }
                }
            }
            items(yields) { yld ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("${yld.quantity} ${yld.unit}", style = MaterialTheme.typography.titleMedium)
                            Text("Rate: ₹${yld.marketRate} / ${yld.unit}", style = MaterialTheme.typography.bodyMedium)
                            
                            val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                            Text(formatter.format(Date(yld.dateMillis)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Row {
                            IconButton(onClick = { yieldBeingEdited = yld; showYieldDialog = true }) { Icon(Icons.Filled.Edit, "Edit") }
                            IconButton(onClick = { 
                                viewModel.deleteYield(yld)
                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar("Yield deleted", "UNDO")
                                    if (result == SnackbarResult.ActionPerformed) viewModel.addOrUpdateYield(yld)
                                }
                            }) { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }

    // --- EXPENSE DIALOG ---
    if (showExpenseDialog) {
        var category by remember { mutableStateOf(expenseBeingEdited?.category ?: "") }
        var cost by remember { mutableStateOf(expenseBeingEdited?.cost?.toString() ?: "") }
        var dateMillis by remember { mutableStateOf(expenseBeingEdited?.dateMillis ?: System.currentTimeMillis()) }
        var showDatePicker by remember { mutableStateOf(false) }
        val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())

        AlertDialog(
            onDismissRequest = { showExpenseDialog = false },
            title = { Text(if (expenseBeingEdited == null) "Add Expense" else "Edit Expense") },
            text = {
                Column {
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    
                    // NEW: Number Keyboard & Prevent typing negative signs
                    OutlinedTextField(
                        value = cost, 
                        onValueChange = { if (!it.contains("-")) cost = it }, 
                        label = { Text("Cost") }, 
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) { Text("Date: ${formatter.format(Date(dateMillis))}") }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val costDouble = cost.toDoubleOrNull()
                    // NEW: Ensure cost is greater than or equal to 0
                    if (category.isNotBlank() && costDouble != null && costDouble >= 0) {
                        viewModel.addOrUpdateExpense(Expense(
                            expenseId = expenseBeingEdited?.expenseId ?: 0,
                            plotId = plotId,
                            category = category,
                            cost = costDouble,
                            dateMillis = dateMillis
                        ))
                        showExpenseDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showExpenseDialog = false }) { Text("Cancel") } }
        )

        if (showDatePicker) {
            val dateState = rememberDatePickerState(
                initialSelectedDateMillis = dateMillis,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean { return utcTimeMillis <= System.currentTimeMillis() }
                }
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = { TextButton(onClick = { dateState.selectedDateMillis?.let { dateMillis = it }; showDatePicker = false }) { Text("OK") } }
            ) { DatePicker(state = dateState) }
        }
    }

    // --- YIELD DIALOG ---
    if (showYieldDialog) {
        var quantity by remember { mutableStateOf(yieldBeingEdited?.quantity?.toString() ?: "") }
        var unit by remember { mutableStateOf(yieldBeingEdited?.unit ?: "kg") }
        var rate by remember { mutableStateOf(yieldBeingEdited?.marketRate?.toString() ?: "") }
        var dateMillis by remember { mutableStateOf(yieldBeingEdited?.dateMillis ?: System.currentTimeMillis()) }
        var showDatePicker by remember { mutableStateOf(false) }
        val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())

        AlertDialog(
            onDismissRequest = { showYieldDialog = false },
            title = { Text(if (yieldBeingEdited == null) "Add Yield" else "Edit Yield") },
            text = {
                Column {
                    // NEW: Number Keyboard & Prevent typing negative signs
                    OutlinedTextField(
                        value = quantity, 
                        onValueChange = { if (!it.contains("-")) quantity = it }, 
                        label = { Text("Quantity") }, 
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit (kg, ton, etc)") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    
                    // NEW: Number Keyboard & Prevent typing negative signs
                    OutlinedTextField(
                        value = rate, 
                        onValueChange = { if (!it.contains("-")) rate = it }, 
                        label = { Text("Rate per Unit") }, 
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) { Text("Date: ${formatter.format(Date(dateMillis))}") }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val qDouble = quantity.toDoubleOrNull()
                    val rDouble = rate.toDoubleOrNull()
                    // NEW: Ensure numbers are greater than or equal to 0
                    if (qDouble != null && rDouble != null && qDouble >= 0 && rDouble >= 0) {
                        viewModel.addOrUpdateYield(CropYield(
                            yieldId = yieldBeingEdited?.yieldId ?: 0,
                            plotId = plotId,
                            quantity = qDouble,
                            unit = unit,
                            marketRate = rDouble,
                            dateMillis = dateMillis
                        ))
                        showYieldDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showYieldDialog = false }) { Text("Cancel") } }
        )

        if (showDatePicker) {
            val dateState = rememberDatePickerState(
                initialSelectedDateMillis = dateMillis,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean { return utcTimeMillis <= System.currentTimeMillis() }
                }
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = { TextButton(onClick = { dateState.selectedDateMillis?.let { dateMillis = it }; showDatePicker = false }) { Text("OK") } }
            ) { DatePicker(state = dateState) }
        }
    }
}