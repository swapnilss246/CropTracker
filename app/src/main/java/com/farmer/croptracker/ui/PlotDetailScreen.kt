package com.farmer.croptracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farmer.croptracker.viewmodel.CropViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlotDetailScreen(
    plotId: Int,
    plotName: String,
    viewModel: CropViewModel,
    onNavigateBack: () -> Unit
) {
    // Collect the data for this specific plot
    val expenses by viewModel.getExpenses(plotId).collectAsState(initial = emptyList())
    val yields by viewModel.getYields(plotId).collectAsState(initial = emptyList())

    // Dialog trackers
    var showExpenseDialog by remember { mutableStateOf(false) }
    var showYieldDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plotName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // --- EXPENSES SECTION ---
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Expenses", style = MaterialTheme.typography.titleLarge)
                    Button(onClick = { showExpenseDialog = true }) { Text("Add") }
                }
            }
            items(expenses) { exp ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(exp.category, style = MaterialTheme.typography.bodyLarge)
                        Text("₹${exp.cost}", style = MaterialTheme.typography.titleMedium) // Used ₹ (Rupee) for India, change if needed!
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // --- YIELDS SECTION ---
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Yields & Harvests", style = MaterialTheme.typography.titleLarge)
                    Button(onClick = { showYieldDialog = true }) { Text("Add") }
                }
            }
            items(yields) { yld ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text("${yld.quantity} ${yld.unit}", style = MaterialTheme.typography.titleMedium)
                        Text("Rate: ₹${yld.marketRate} per ${yld.unit}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

    // --- EXPENSE DIALOG ---
    if (showExpenseDialog) {
        var category by remember { mutableStateOf("") }
        var cost by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showExpenseDialog = false },
            title = { Text("Add Expense") },
            text = {
                Column {
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Seed, Labor, etc.)") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Total Cost") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    val costDouble = cost.toDoubleOrNull()
                    if (category.isNotBlank() && costDouble != null) {
                        viewModel.addExpense(plotId, category, costDouble)
                        showExpenseDialog = false
                    }
                }) { Text("Save") }
            }
        )
    }

    // --- YIELD DIALOG ---
    if (showYieldDialog) {
        var quantity by remember { mutableStateOf("") }
        var unit by remember { mutableStateOf("kg") }
        var rate by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showYieldDialog = false },
            title = { Text("Add Yield") },
            text = {
                Column {
                    OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit (kg, ton, box)") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = rate, onValueChange = { rate = it }, label = { Text("Market Rate (Per Unit)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    val qDouble = quantity.toDoubleOrNull()
                    val rDouble = rate.toDoubleOrNull()
                    if (qDouble != null && rDouble != null) {
                        viewModel.addYield(plotId, qDouble, unit, rDouble)
                        showYieldDialog = false
                    }
                }) { Text("Save") }
            }
        )
    }
}