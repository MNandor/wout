package com.mnandor.wout.presentation.schedule

import android.util.Log
import androidx.lifecycle.*
import com.mnandor.wout.data.ExerciseDatabase
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.data.entities.KeyValue
import com.mnandor.wout.data.entities.Location
import com.mnandor.wout.data.entities.ScheduleDay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScheduleViewModel(private val database: ExerciseDatabase) : ViewModel() {

    val schedule = MutableLiveData<Pair<String, String>>()

    val allDayTemplates: LiveData<List<Location>> = database.dao().getUniqueDayTemplates().asLiveData()

    val allScheduleDays: LiveData<List<ScheduleDay>> = database.dao().getDaySchedules().asLiveData()

    fun setLoopAndOffset(total: Int, offset: Int){
        GlobalScope.launch { database.dao().setValue(KeyValue("scheduleTotal", total.toString()))}
        GlobalScope.launch { database.dao().setValue(KeyValue("scheduleOffset", offset.toString()))}
    }

    fun getValuesFromDB(){
        GlobalScope.launch {
            val total = database.dao().getValue("scheduleTotal")
            val offset = database.dao().getValue("scheduleOffset")
            schedule.postValue(Pair(total, offset) as Pair<String, String>?)
        }
    }

    fun updateScheduleDay(day: Int, locationName: String){
        val locationID = allDayTemplates.value?.find { it.template == locationName }?.itemID
            ?: return
        Log.d("nandorss", locationID.toString()+day)
        GlobalScope.launch {
            database.dao().addScheduleDay(ScheduleDay(
                day,
                locationID!!,
                ""
            ))
        }
    }

    fun removeScheduleDayData(day: Int){
        GlobalScope.launch {
            database.dao().removeScheduleDayData(day)
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