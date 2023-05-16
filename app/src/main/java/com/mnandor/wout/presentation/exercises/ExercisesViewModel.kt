package com.mnandor.wout.presentation.exercises

import androidx.lifecycle.*
import com.mnandor.wout.data.ExerciseDatabase
import com.mnandor.wout.data.entities.Exercise
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ExercisesViewModel(private val database: ExerciseDatabase) : ViewModel() {

    val allTemplates: LiveData<List<Exercise>> = database.dao().getTemplates().asLiveData()

    fun insert(template: Exercise) {
        // todo obvious workaround is obvious
        GlobalScope.launch { database.dao().addExerciseTemplate(template)}
    }
}

class ExercisesViewModelFactory(private val database: ExerciseDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExercisesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExercisesViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}