package com.farmer.croptracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmer.croptracker.data.Expense
import com.farmer.croptracker.data.CropYield
import com.farmer.croptracker.data.Plot
import com.farmer.croptracker.data.PlotDao
import com.farmer.croptracker.data.Treatment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CropViewModel(private val plotDao: PlotDao) : ViewModel() {

    // --- PLOTS ---
    val allPlots: StateFlow<List<Plot>> = plotDao.getAllPlots()
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())
    val archivedPlots: Flow<List<Plot>> = plotDao.getArchivedPlots() // NEW
    val deletedPlots: Flow<List<Plot>> = plotDao.getDeletedPlots()

    fun addOrUpdatePlot(plot: Plot) = viewModelScope.launch { plotDao.insertPlot(plot) }
    fun archivePlot(plot: Plot) = viewModelScope.launch { plotDao.archivePlot(plot.id) } // NEW
    fun unarchivePlot(plot: Plot) = viewModelScope.launch { plotDao.unarchivePlot(plot.id) } // NEW
    fun deletePlot(plot: Plot) = viewModelScope.launch { plotDao.softDeletePlot(plot.id, System.currentTimeMillis()) }
    fun restorePlot(plot: Plot) = viewModelScope.launch { plotDao.restorePlot(plot.id) }
    fun permanentlyDeletePlot(plot: Plot) = viewModelScope.launch { plotDao.permanentlyDeletePlot(plot) }

    // --- EXPENSES ---
    fun getExpenses(plotId: Int): Flow<List<Expense>> = plotDao.getExpensesForPlot(plotId)
    fun getDeletedExpenses(plotId: Int): Flow<List<Expense>> = plotDao.getDeletedExpensesForPlot(plotId)
    
    fun addOrUpdateExpense(expense: Expense) = viewModelScope.launch { plotDao.insertExpense(expense) }
    fun deleteExpense(expense: Expense) = viewModelScope.launch { plotDao.softDeleteExpense(expense.expenseId, System.currentTimeMillis()) }
    fun restoreExpense(expense: Expense) = viewModelScope.launch { plotDao.restoreExpense(expense.expenseId) }
    fun permanentlyDeleteExpense(expense: Expense) = viewModelScope.launch { plotDao.permanentlyDeleteExpense(expense) }

    // --- YIELDS ---
    fun getYields(plotId: Int): Flow<List<CropYield>> = plotDao.getYieldsForPlot(plotId)
    fun getDeletedYields(plotId: Int): Flow<List<CropYield>> = plotDao.getDeletedYieldsForPlot(plotId)

    fun addOrUpdateYield(yield: CropYield) = viewModelScope.launch { plotDao.insertYield(yield) }
    fun deleteYield(yield: CropYield) = viewModelScope.launch { plotDao.softDeleteYield(yield.yieldId, System.currentTimeMillis()) }
    fun restoreYield(yield: CropYield) = viewModelScope.launch { plotDao.restoreYield(yield.yieldId) }
    fun permanentlyDeleteYield(yield: CropYield) = viewModelScope.launch { plotDao.permanentlyDeleteYield(yield) }

    // --- TREATMENTS (NEW) ---
    fun getTreatments(plotId: Int): Flow<List<Treatment>> = plotDao.getTreatmentsForPlot(plotId)
    fun getDeletedTreatments(plotId: Int): Flow<List<Treatment>> = plotDao.getDeletedTreatmentsForPlot(plotId)

    fun addOrUpdateTreatment(treatment: Treatment) = viewModelScope.launch { plotDao.insertTreatment(treatment) }
    fun deleteTreatment(treatment: Treatment) = viewModelScope.launch { plotDao.softDeleteTreatment(treatment.treatmentId, System.currentTimeMillis()) }
    fun restoreTreatment(treatment: Treatment) = viewModelScope.launch { plotDao.restoreTreatment(treatment.treatmentId) }
    fun permanentlyDeleteTreatment(treatment: Treatment) = viewModelScope.launch { plotDao.permanentlyDeleteTreatment(treatment) }
}

class CropViewModelFactory(private val plotDao: PlotDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CropViewModel::class.java)) return CropViewModel(plotDao) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}