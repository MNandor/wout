package com.mnandor.wout.presentation.schedule

import androidx.lifecycle.*
import com.mnandor.wout.data.ExerciseDatabase
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.data.entities.KeyValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScheduleViewModel(private val database: ExerciseDatabase) : ViewModel() {


    val allTemplates: LiveData<List<Exercise>> = database.dao().getTemplates().asLiveData()

    val scheduleTotal = MutableLiveData<String>()
    val scheduleOffset = MutableLiveData<String>()

    fun setLoopAndOffset(total: Int, offset: Int){
        GlobalScope.launch { database.dao().setValue(KeyValue("scheduleTotal", total.toString()))}
        GlobalScope.launch { database.dao().setValue(KeyValue("scheduleOffset", offset.toString()))}
    }

    fun getValuesFromDB(){
        GlobalScope.launch {
            scheduleTotal.postValue(database.dao().getValue("scheduleTotal"))
            scheduleOffset.postValue(database.dao().getValue("scheduleOffset"))
        }
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