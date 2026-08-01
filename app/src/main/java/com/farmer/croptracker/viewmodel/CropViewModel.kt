package com.farmer.croptracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmer.croptracker.data.Expense
import com.farmer.croptracker.data.CropYield
import com.farmer.croptracker.data.Plot
import com.farmer.croptracker.data.PlotDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CropViewModel(private val plotDao: PlotDao) : ViewModel() {

    val allPlots: StateFlow<List<Plot>> = plotDao.getAllPlots()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addPlot(plotName: String, cropName: String, cropBreed: String) {
        viewModelScope.launch {
            plotDao.insertPlot(Plot(plotName = plotName, cropName = cropName, cropBreed = cropBreed))
        }
    }

    // --- NEW: Get Data for a specific plot ---
    fun getExpenses(plotId: Int): Flow<List<Expense>> {
        return plotDao.getExpensesForPlot(plotId)
    }

    fun getYields(plotId: Int): Flow<List<CropYield>> {
        return plotDao.getYieldsForPlot(plotId)
    }

    // --- NEW: Save Data ---
    fun addExpense(plotId: Int, category: String, cost: Double) {
        viewModelScope.launch {
            val expense = Expense(
                plotId = plotId,
                category = category,
                cost = cost,
                dateMillis = System.currentTimeMillis() // Automatically save the exact time
            )
            plotDao.insertExpense(expense)
        }
    }

    fun addYield(plotId: Int, quantity: Double, unit: String, marketRate: Double) {
        viewModelScope.launch {
            val yield = CropYield(
                plotId = plotId,
                quantity = quantity,
                unit = unit,
                marketRate = marketRate,
                dateMillis = System.currentTimeMillis() // Automatically save the exact time
            )
            plotDao.insertYield(yield)
        }
    }

    // --- NEW: Delete Plot ---
    fun deletePlot(plotId: Int) {
        viewModelScope.launch {
            plotDao.deletePlotById(plotId)
        }
    }
}

class CropViewModelFactory(private val plotDao: PlotDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CropViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CropViewModel(plotDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}