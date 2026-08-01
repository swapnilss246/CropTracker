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

    // --- PLOTS ---
    val allPlots: StateFlow<List<Plot>> = plotDao.getAllPlots()
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    fun addOrUpdatePlot(plot: Plot) {
        viewModelScope.launch { plotDao.insertPlot(plot) } // REPLACE handles both add and update!
    }

    fun deletePlot(plot: Plot) {
        viewModelScope.launch { plotDao.deletePlot(plot) }
    }

    // --- EXPENSES ---
    fun getExpenses(plotId: Int): Flow<List<Expense>> = plotDao.getExpensesForPlot(plotId)

    fun addOrUpdateExpense(expense: Expense) {
        viewModelScope.launch { plotDao.insertExpense(expense) }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { plotDao.deleteExpense(expense) }
    }

    // --- YIELDS ---
    fun getYields(plotId: Int): Flow<List<CropYield>> = plotDao.getYieldsForPlot(plotId)

    fun addOrUpdateYield(yield: CropYield) {
        viewModelScope.launch { plotDao.insertYield(yield) }
    }

    fun deleteYield(yield: CropYield) {
        viewModelScope.launch { plotDao.deleteYield(yield) }
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