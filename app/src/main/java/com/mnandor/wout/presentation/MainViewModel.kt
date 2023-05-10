package com.mnandor.wout.presentation

import androidx.lifecycle.*
import com.mnandor.wout.data.ExerciseDatabase
import com.mnandor.wout.data.entities.ExerciseLog
import com.mnandor.wout.data.entities.ExerciseTemplate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainViewModel(private val database: ExerciseDatabase) : ViewModel() {

    val filter = MutableLiveData<String>()

    fun setFilter(filterStr: String){
        GlobalScope.launch { filter.postValue(filterStr) }
    }

    val allVisibleTemplates: LiveData<List<ExerciseTemplate>> = filter.switchMap {
        database.dao().getNonhiddenTemplates(it).asLiveData()
    }



    val allLogs: LiveData<List<ExerciseLog>> = database.dao().getLogs().asLiveData()

    val trendlinePrediction: MutableLiveData<Int> = MutableLiveData()

    val allDayTemplates: LiveData<List<String>> = database.dao().getDayTemplateNames().asLiveData()

    fun insert(log: ExerciseLog) {
        // todo obvious workaround is obvious
        GlobalScope.launch { database.dao().addExerciseLog(log)}
    }

    fun deleteExerciseLog(log: ExerciseLog){
        GlobalScope.launch { database.dao().deleteLog(log)}
    }

    fun updateExerciseLog(log: ExerciseLog){
        GlobalScope.launch { database.dao().updateLog(log)}
    }

    public fun calculateTrendline(template: ExerciseTemplate){
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