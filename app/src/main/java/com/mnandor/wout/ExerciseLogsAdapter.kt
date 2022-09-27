package com.mnandor.wout

import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ExerciseLogsAdapter : RecyclerView.Adapter<ExerciseLogsAdapter.ExerciseLogViewHolder>() {



    companion object{
        val TYPE_ITEM = 0
        val TYPE_SEPARATOR = 1
    }


    private var items:List<ExerciseLog> = listOf()
    private lateinit var deleteCallback: (ExerciseLog) -> Unit
    private lateinit var editCallback: (ExerciseLog) -> Unit

    var dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")


    fun setDeleteCallback(callback: (ExerciseLog) -> Unit){
        deleteCallback = callback
    }

    fun setEditCallback(callback: (ExerciseLog) -> Unit){
        editCallback = callback
    }


    private val holeyItems:ArrayList<ExerciseLog?> = ArrayList()
    private val holeyDates:ArrayList<LocalDate?> = ArrayList()
    fun setItems(newItems:List<ExerciseLog>){
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

    fun callDeleteCallback(item: ExerciseLog){
        deleteCallback(item)
    }

    fun callEditCallback(item: ExerciseLog){
        editCallback(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseLogViewHolder {
        return ExerciseLogViewHolder.create(parent, viewType)
    }

    override fun onBindViewHolder(holder: ExerciseLogViewHolder, position: Int) {
        if(holeyItems[position] != null){
            val current = holeyItems[position]!!
            holder.bind(current)
            holder.itemView.setOnLongClickListener{
                callDeleteCallback(current)
                return@setOnLongClickListener true
            }
            holder.itemView.setOnClickListener {
                callEditCallback(current)
            }
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


        fun bind(item: ExerciseLog) {
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
        if (holeyDates[position] == null)
            return TYPE_ITEM
        else
            return TYPE_SEPARATOR
    }

}