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

    // --- ACTIVE DATA ---
    val allPlots: StateFlow<List<Plot>> = plotDao.getAllPlots()
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    // --- DELETED DATA (For Recycle Bin) ---
    val deletedPlots: Flow<List<Plot>> = plotDao.getDeletedPlots()
    val deletedExpenses: Flow<List<Expense>> = plotDao.getDeletedExpenses()
    val deletedYields: Flow<List<CropYield>> = plotDao.getDeletedYields()

    // --- PLOTS ---
    fun addOrUpdatePlot(plot: Plot) = viewModelScope.launch { plotDao.insertPlot(plot) }
    fun deletePlot(plot: Plot) = viewModelScope.launch { plotDao.softDeletePlot(plot.id) } // Soft Delete!
    fun restorePlot(plot: Plot) = viewModelScope.launch { plotDao.restorePlot(plot.id) }
    fun permanentlyDeletePlot(plot: Plot) = viewModelScope.launch { plotDao.permanentlyDeletePlot(plot) }

    // --- EXPENSES ---
    fun getExpenses(plotId: Int): Flow<List<Expense>> = plotDao.getExpensesForPlot(plotId)
    fun addOrUpdateExpense(expense: Expense) = viewModelScope.launch { plotDao.insertExpense(expense) }
    fun deleteExpense(expense: Expense) = viewModelScope.launch { plotDao.softDeleteExpense(expense.expenseId) } // Soft Delete!
    fun restoreExpense(expense: Expense) = viewModelScope.launch { plotDao.restoreExpense(expense.expenseId) }
    fun permanentlyDeleteExpense(expense: Expense) = viewModelScope.launch { plotDao.permanentlyDeleteExpense(expense) }

    // --- YIELDS ---
    fun getYields(plotId: Int): Flow<List<CropYield>> = plotDao.getYieldsForPlot(plotId)
    fun addOrUpdateYield(yield: CropYield) = viewModelScope.launch { plotDao.insertYield(yield) }
    fun deleteYield(yield: CropYield) = viewModelScope.launch { plotDao.softDeleteYield(yield.yieldId) } // Soft Delete!
    fun restoreYield(yield: CropYield) = viewModelScope.launch { plotDao.restoreYield(yield.yieldId) }
    fun permanentlyDeleteYield(yield: CropYield) = viewModelScope.launch { plotDao.permanentlyDeleteYield(yield) }
}

class CropViewModelFactory(private val plotDao: PlotDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CropViewModel::class.java)) return CropViewModel(plotDao) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}