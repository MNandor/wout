package com.mnandor.wout.presentation

import androidx.lifecycle.*
import com.mnandor.wout.data.ExerciseDatabase
import com.mnandor.wout.data.entities.Exercise
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScheduleViewModel(private val database: ExerciseDatabase) : ViewModel() {

    val allTemplates: LiveData<List<Exercise>> = database.dao().getTemplates().asLiveData()

    fun insert(template: Exercise) {
        // todo obvious workaround is obvious
        GlobalScope.launch { database.dao().addExerciseTemplate(template)}
    }
}

class ScheduleViewModelFactory(private val database: ExerciseDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}