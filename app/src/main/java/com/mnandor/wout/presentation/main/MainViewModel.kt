package com.mnandor.wout.presentation.main

import android.util.Log
import androidx.lifecycle.*
import com.mnandor.wout.DateUtility
import com.mnandor.wout.data.ExerciseDatabase
import com.mnandor.wout.data.entities.Completion
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.data.entities.KeyValue
import com.mnandor.wout.data.entities.ScheduleDay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainViewModel(private val database: ExerciseDatabase) : ViewModel() {

    val filter = MutableLiveData<String>()


    fun setFilter(filterStr: String){
        GlobalScope.launch { filter.postValue(filterStr) }
    }

    val allVisibleTemplates: LiveData<List<Exercise>> = filter.switchMap {
        database.dao().getNonhiddenTemplates(it).asLiveData()
    }


    val locationSetting = MutableLiveData<String>()
    fun loadLocationSetting(){
        GlobalScope.launch {
            val today = DateUtility.getToday()
            val lastDay = database.dao().getValue("lastDay")

            Log.d("nandorss", today+" - "+lastDay)

            // if the day was set already today, use it
            var day:ScheduleDay?
            if (today == lastDay){
                day = database.dao().getDayByNumber(-1)
            } else {

                val total = database.dao().getValue("scheduleTotal")!!.toInt()
                val offset = database.dao().getValue("scheduleOffset")!!.toInt()

                var value = (offset+ DateUtility.offsetModifier(total) +total).toInt()%total
                value = (value-1+total) % total

                Log.d("nandorss", "Get schedule "+value.toString())

                day = database.dao().getDayByNumber(value)

                if (day == null){
                    // No value set for this day
                    locationSetting.postValue("All")
                    return@launch
                }

                val settableDay = ScheduleDay(-1, day.location, day.exercises)
                database.dao().setDaySchedule(settableDay)
//                database.dao().setValue(KeyValue("lastDay", today))
            }

            Log.d("nandorss", ">"+day.toString())

            val location = database.dao().getLocationByID(day.location)

            Log.d("nandorss", ">"+location.toString())

            locationSetting.postValue(location.template)

        }
    }



    val allLogs: LiveData<List<Completion>> = database.dao().getLogs().asLiveData()

    val trendlinePrediction: MutableLiveData<Int> = MutableLiveData()

    val allDayTemplates: LiveData<List<String>> = database.dao().getDayTemplateNames().asLiveData()

    fun insert(log: Completion) {
        // todo obvious workaround is obvious
        GlobalScope.launch { database.dao().addExerciseLog(log)}
    }

    fun deleteExerciseLog(log: Completion){
        GlobalScope.launch { database.dao().deleteLog(log)}
    }

    fun updateExerciseLog(log: Completion){
        GlobalScope.launch { database.dao().updateLog(log)}
    }

    val openCount = MutableLiveData<String>()
    fun storeAppOpened(){

        GlobalScope.launch {
            val last:String? = database.dao().getValue("openCounter")

            if (last != null) {
                val next = (last.toInt()+1).toString()
                database.dao().setValue(KeyValue("openCounter", next))
                openCount.postValue(next)

            } else {
                database.dao().setValue(KeyValue("openCounter", "1"))
                openCount.postValue("1")
            }

        }
    }

    public fun calculateTrendline(template: Exercise){
        GlobalScope.launch {
            val COUNT = 5

            var points = database.dao().getTrendlinePoints(template.name, COUNT)



            // do the trendline calculation
            // this can be expanded on in many ways
            //  have the calculation weighted towards more recent
            //  handle not just set values but dates
            //  switch from set to reps*set and maximums

            val n = points.size
            val x = (0..n-1).toList()
            val y = points

            if (n == 0){
                trendlinePrediction.postValue(0)
                return@launch
            }

            val xy = mutableListOf<Int>()

            for(it in x){
                xy.add(it* y[it]!!)
            }

            val xs = x.map { it*it }

            val b = (n*xy.sum() - x.sum()*(y.sum())).toFloat() / (n*xs.sum() - (x.sum()*x.sum()))

            val a = (y.sum() - b*x.sum()) / n

            var predict = a + b*-1
            if (predict < y.max() || predict.isNaN())
                predict = y.max().toFloat()

            trendlinePrediction.postValue(predict.roundToInt())

        }

    }

    fun figureOutTodaysLocation(){

    }
}

class MainViewModelFactory(private val database: ExerciseDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}