package com.mnandor.wout.presentation.graph

import androidx.lifecycle.*
import com.mnandor.wout.data.ExerciseDatabase
import com.mnandor.wout.data.entities.Exercise
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GraphViewModel(private val database: ExerciseDatabase) : ViewModel() {


}

class GraphViewModelFactory(private val database: ExerciseDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GraphViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GraphViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}