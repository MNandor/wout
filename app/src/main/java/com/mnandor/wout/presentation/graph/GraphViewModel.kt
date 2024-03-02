package com.mnandor.wout.presentation.graph

import android.util.Log
import androidx.lifecycle.*
import com.mnandor.wout.DateUtility
import com.mnandor.wout.data.ExerciseDatabase
import com.mnandor.wout.data.entities.Completion
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class GraphViewModel(private val database: ExerciseDatabase) : ViewModel() {

    val filteredLogs = MutableLiveData<List<Completion>>()
    fun getLast30DaysOfRelevantLogs(exerciseName: String){
        val date = Date()
        val cal: Calendar = Calendar.getInstance()
        cal.setTime(date)
        cal.add(Calendar.DAY_OF_MONTH, -30)
        val today = cal.getTime().getTime() / 1000;
        GlobalScope.launch {
            val logs = database.dao().getLogs().first()
            val filtered = logs.filter {
                it.exercise == exerciseName && DateUtility.parseFormatToUnix(it.timestamp) > today
            }
            filteredLogs.postValue(filtered)
        }
    }


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