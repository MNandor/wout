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

            updateValues()
        }
    }

    private var summarized: Boolean = false

    fun setSummarizeToggle(summarized: Boolean){
        Log.d("GraphView", "Summarized: was ${this.summarized}; now is ${summarized}")
        this.summarized = summarized

        updateValues()
    }

    private var trait: String = "Sets"
    fun setTrait(trait: String){
        Log.d("GraphView", "Trait: $trait")
        this.trait = trait

        updateValues()
    }


    val data = MutableLiveData<List<Pair<String, Float>>>()
    fun updateValues(){
        val logs = filteredLogs.value

        val pairs = when (trait){
            "Sets" -> logs?.map { it -> Pair(it.timestamp, it.sets) }
            "Reps" -> logs?.map { it -> Pair(it.timestamp, it.reps) }
            "Weight" -> logs?.map { it -> Pair(it.timestamp, it.weight) }
            "Distance" -> logs?.map { it -> Pair(it.timestamp, it.distance) }
            //"Time" -> logs?.map { it -> Pair(it.timestamp, it.duration) }
            else -> return
        }

        Log.d("GraphView", "$pairs")

        if (summarized){
            val grouped = pairs?.groupBy { DateUtility.getDateOnlyFromFull(it.first) }

            val numeric = when(trait) {
                "Sets" -> grouped?.map { it -> Pair(it.key, it.value.sumOf { it.second.toString().toInt() }) }
                "Reps" -> grouped?.map  { it -> Pair(it.key, it.value.sumOf { it.second.toString().toInt() }) }
                "Weight" -> grouped?.map  { it -> Pair(it.key, it.value.maxOf { it.second.toString().toFloat() }) }
                "Distance" -> grouped?.map  { it -> Pair(it.key, it.value.sumOf { it.second.toString().toFloat().toInt() }) }
                //"Time" -> grouped?.map  { it -> Pair(it.key, it.value.sumOf { it.second.toString().toInt() }) }
                else -> return
            }
            Log.d("GraphView", "$numeric")

            val final = numeric?.sortedBy { it.first }?.take(30)?.map { it -> Pair(it.first, it.second.toFloat()) }

            if (final != null)
                data.postValue(final!!)

        } else {
            val final = pairs?.sortedBy { it.first }?.take(30)?.map { it -> Pair(it.first, it.second?.toFloat() ?: 0.0f) }

            if (final != null)
                data.postValue(final!!)
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