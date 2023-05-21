package com.mnandor.wout.presentation.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mnandor.wout.R
import com.mnandor.wout.data.entities.Completion
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainRecyclerAdapter : RecyclerView.Adapter<MainRecyclerAdapter.ExerciseLogViewHolder>() {



    companion object{
        val TYPE_ITEM = 0
        val TYPE_SEPARATOR = 1
        val TYPE_ITEM_TOP = 2
        val TYPE_ITEM_BOTTOM = 3
        val TYPE_ITEM_MIDDLE = 4
    }


    private var items:List<Completion> = listOf()
    private lateinit var deleteCallback: (Completion) -> Unit
    private lateinit var editCallback: (Completion) -> Unit
    private var relevantExercises:List<String> = listOf()

    var dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")


    fun setDeleteCallback(callback: (Completion) -> Unit){
        deleteCallback = callback
    }

    fun setEditCallback(callback: (Completion) -> Unit){
        editCallback = callback
    }

    fun setRelevantExercises(exercises: List<String>){
        relevantExercises = exercises
    }


    private val holeyItems:ArrayList<Completion?> = ArrayList()
    private val holeyDates:ArrayList<LocalDate?> = ArrayList()
    fun setItems(newItems:List<Completion>){
        items = newItems
        holeyItems.clear()
        holeyDates.clear()
        val today: LocalDate = LocalDateTime.now().toLocalDate()

        if (items.isEmpty()){
            holeyDates.add(today)
            holeyItems.add(null)
        } else{
            var firstDay = LocalDateTime.parse(items[0].timestamp, dtf).toLocalDate()

            for (item in items){
                val itemDate = LocalDateTime.parse(item.timestamp, dtf).toLocalDate()
                while (firstDay <= itemDate){
                    holeyDates.add(firstDay)
                    holeyItems.add(null)
                    firstDay = firstDay.plusDays(1)
                }

                holeyDates.add(null)
                holeyItems.add(item)
            }

            while (firstDay <= today){
                holeyDates.add(firstDay)
                holeyItems.add(null)
                firstDay = firstDay.plusDays(1)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseLogViewHolder {
        return ExerciseLogViewHolder.create(parent, viewType)
    }

    override fun onBindViewHolder(holder: ExerciseLogViewHolder, position: Int) {
        if(holeyItems[position] != null){
            val current = holeyItems[position]!!
            holder.bind(current)
            holder.itemView.setOnLongClickListener{
                deleteCallback(current)
                return@setOnLongClickListener true
            }
            holder.itemView.setOnClickListener {
                editCallback(current)
            }
            if (relevantExercises.contains(current.exercise))
                holder.itemView.alpha = 1F
            else
                holder.itemView.alpha = 0.5F
        } else {
            holder.bind(holeyDates[position]!!)

        }
    }


    override fun getItemCount(): Int {
        return holeyItems.size
    }

    class ExerciseLogViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        var dtfhuman: DateTimeFormatter = DateTimeFormatter.ofPattern("LLLL dd EEEE")
        private var isSeparator = false


        fun bind(item: Completion) {
            val wordItemView: TextView = itemView.findViewById(R.id.exerciseTemplateNameTV)
            val textLogDate: TextView = itemView.findViewById(R.id.textLogDate)
            val textLogInfo: TextView = itemView.findViewById(R.id.textLogInfo)

            wordItemView.text = item.exercise
            textLogDate.text = item.timestamp

            textLogInfo.text = ""

            if (item.duration != null) textLogInfo.text = textLogInfo.text.toString()+" Time: "+item.duration.toString()
            if (item.distance != null) textLogInfo.text = textLogInfo.text.toString()+" Distance: "+item.distance.toString()
            if (item.weight != null) textLogInfo.text = textLogInfo.text.toString()+" Weight: "+item.weight.toString()
            if (item.sets != null) textLogInfo.text = textLogInfo.text.toString()+" Sets: "+item.sets.toString()
            if (item.reps != null) textLogInfo.text = textLogInfo.text.toString()+" Reps: "+item.reps.toString()

        }

        fun bind(date:LocalDate){
            val wordItemView: TextView = itemView.findViewById(R.id.exerciseTemplateNameTV)
            wordItemView.text = dtfhuman.format(date)
        }

        companion object {
            fun create(parent: ViewGroup, type: Int): ExerciseLogViewHolder {

                if (type == TYPE_ITEM){
                    val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_exercise_log, parent, false)
                    return ExerciseLogViewHolder(view)
                } else if (type == TYPE_ITEM_TOP) {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_exercise_log_top, parent, false)
                    return ExerciseLogViewHolder(view)

                } else if (type == TYPE_ITEM_BOTTOM){
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_exercise_log_bottom, parent, false)
                    return ExerciseLogViewHolder(view)
                } else if (type == TYPE_ITEM_MIDDLE){
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_exercise_log_middle, parent, false)
                    return ExerciseLogViewHolder(view)
                }
                else {
                    val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_log_date_separator, parent, false)
                    val vh = ExerciseLogViewHolder(view)
                    vh.isSeparator = true

                    return vh
                }

            }
        }
    }

    @Override
    override fun getItemViewType(position: Int): Int {
        if (holeyDates[position] == null) {
            val nextMatch:Boolean = position+1 < holeyItems.size && holeyItems[position+1] != null &&
                    holeyItems[position]?.exercise ?: null == holeyItems[position+1]?.exercise ?: null
            val prevMatch:Boolean = position-1 >= 0 && holeyItems[position-1] != null &&
                    holeyItems[position]?.exercise ?: null == holeyItems[position-1]?.exercise ?: null

            if (nextMatch && prevMatch)
                return TYPE_ITEM_MIDDLE
            else if (nextMatch)
                return TYPE_ITEM_TOP
            else if (prevMatch)
                return TYPE_ITEM_BOTTOM
            else
                return TYPE_ITEM
        }
        else{
            return TYPE_SEPARATOR
        }
    }

}