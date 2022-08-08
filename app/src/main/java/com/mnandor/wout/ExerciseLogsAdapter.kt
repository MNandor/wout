package com.mnandor.wout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExerciseLogsAdapter : RecyclerView.Adapter<ExerciseLogsAdapter.ExerciseLogViewHolder>() {

    private var items:List<ExerciseLog> = listOf()

    fun setItems(newItems:List<ExerciseLog>){
        items = newItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseLogViewHolder {
        return ExerciseLogViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ExerciseLogViewHolder, position: Int) {
        val current = items[position]
        holder.bind(current)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ExerciseLogViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val wordItemView: TextView = itemView.findViewById(R.id.exerciseTemplateNameTV)
        private val textLogDate: TextView = itemView.findViewById(R.id.textLogDate)
        private val textLogInfo: TextView = itemView.findViewById(R.id.textLogInfo)

        fun bind(item: ExerciseLog) {
            wordItemView.text = item.exercise
            textLogDate.text = item.timestamp

            if (item.duration != null) textLogInfo.text = textLogInfo.text.toString()+" Time: "+item.duration.toString()
            if (item.distance != null) textLogInfo.text = textLogInfo.text.toString()+" Distance: "+item.distance.toString()
            if (item.weight != null) textLogInfo.text = textLogInfo.text.toString()+" Weight: "+item.weight.toString()
            if (item.sets != null) textLogInfo.text = textLogInfo.text.toString()+" Sets: "+item.sets.toString()
            if (item.reps != null) textLogInfo.text = textLogInfo.text.toString()+" Reps: "+item.reps.toString()

        }

        companion object {
            fun create(parent: ViewGroup): ExerciseLogViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_exercise_log, parent, false)
                return ExerciseLogViewHolder(view)
            }
        }
    }

}