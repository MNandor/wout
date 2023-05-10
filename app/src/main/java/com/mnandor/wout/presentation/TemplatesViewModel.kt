package com.mnandor.wout.presentation

import androidx.lifecycle.*
import com.mnandor.wout.data.ExerciseDatabase
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.data.entities.Location
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TemplatesViewModel(private val database: ExerciseDatabase) : ViewModel() {
    val allVisibleTemplates: LiveData<List<Exercise>> = database.dao().getTemplates().asLiveData()
    val allLocations: LiveData<List<Location>> = database.dao().getDayTemplates().asLiveData()

    fun insert(item: Location){
        GlobalScope.launch { database.dao().addDayTemplate(item)}
    }

    fun remove(item: Location){
        GlobalScope.launch { database.dao().deleteDayTemplate(item)}
    }



}

class TemplatesViewModelFactory(private val database: ExerciseDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TemplatesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TemplatesViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}