package com.mnandor.wout.presentation.schedule

import android.util.Log
import androidx.lifecycle.*
import com.mnandor.wout.DateUtility
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
    val allDayTemplateNames: LiveData<List<String>> = database.dao().getDayTemplateNames().asLiveData()

    val allScheduleDays: LiveData<List<ScheduleDay>> = database.dao().getDaySchedules().asLiveData()

    fun setFilter(filterStr: String){

        Log.d("nandorsss", filterStr)

        GlobalScope.launch {

            if (filterStr == "All"){
                database.dao().deleteScheduleDayByID(-1)
                return@launch
            }

            val today = DateUtility.getToday()

            database.dao().setValue(KeyValue("lastDay", today))

            val loc = database.dao().getLocationByName(filterStr)

            Log.d("nandorsss", loc.toString())

            val settableDay = ScheduleDay(-1, loc.itemID, "")
            database.dao().addScheduleDay(settableDay)

        }
    }

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

    val locationSetting = MutableLiveData<String>()
    fun loadLocationSetting(){
        GlobalScope.launch {
            val today = DateUtility.getToday()
            val lastDay = database.dao().getValue("lastDay")

            Log.d("nandorsss", today+" - "+lastDay)

            // if the day was set already today, use it
            var day:ScheduleDay?
            if (today == lastDay){
                day = database.dao().getDayByNumber(-1)
            } else {

                val total = database.dao().getValue("scheduleTotal")?.toInt() ?: 1
                val offset = database.dao().getValue("scheduleOffset")?.toInt() ?: 1

                var value = (offset+ DateUtility.offsetModifier(total) +total).toInt()%total
                value = (value-1+total) % total

                Log.d("nandorsss", "Get schedule "+value.toString())

                day = database.dao().getDayByNumber(value)

            }

            if (day == null){
                // No value set for this day
                locationSetting.postValue("All")
                return@launch
            }

            Log.d("nandorss", ">"+day.toString())

            val location = database.dao().getLocationByID(day.location)

            Log.d("nandorss", ">"+location.toString())

            locationSetting.postValue(location.template)

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