package com.farmer.croptracker.viewmodel // Change to your package

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmer.croptracker.data.Plot
import com.farmer.croptracker.data.PlotDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CropViewModel(private val plotDao: PlotDao) : ViewModel() {

    // 1. This grabs the live stream of data (Flow) from the DAO and converts it 
    // into a StateFlow. The UI will "observe" this and redraw itself automatically 
    // whenever a new plot is added!
    val allPlots: StateFlow<List<Plot>> = plotDao.getAllPlots()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. This function saves a new plot. 'viewModelScope.launch' ensures 
    // this heavy database work happens on a background thread so the app doesn't freeze.
    fun addPlot(plotName: String, cropName: String, cropBreed: String) {
        viewModelScope.launch {
            val newPlot = Plot(
                plotName = plotName,
                cropName = cropName,
                cropBreed = cropBreed
            )
            plotDao.insertPlot(newPlot)
        }
    }
}

// 3. This Factory is a standard Android pattern required to pass the DAO into the ViewModel
class CropViewModelFactory(private val plotDao: PlotDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CropViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CropViewModel(plotDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
