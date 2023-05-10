package com.mnandor.wout.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mnandor.wout.R
import com.mnandor.wout.data.entities.Exercise

class ExerciseTemplatesAdapter : RecyclerView.Adapter<ExerciseTemplatesAdapter.ExerciseTemplateViewHolder>() {

    private var items:List<Exercise> = listOf()

    fun setItems(newItems:List<Exercise>){
        items = newItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseTemplateViewHolder {
        return ExerciseTemplateViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ExerciseTemplateViewHolder, position: Int) {
        val current = items[position]
        holder.bind(current)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ExerciseTemplateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val wordItemView: TextView = itemView.findViewById(R.id.exerciseTemplateNameTV)

        fun bind(item: Exercise) {
            wordItemView.text = item.name
            if (item.isDisabled){
                wordItemView.alpha = 0.5F
            } else {
                wordItemView.alpha = 1F
            }
        }

        companion object {
            fun create(parent: ViewGroup): ExerciseTemplateViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_exercise_template, parent, false)
                return ExerciseTemplateViewHolder(view)
            }
        }
    }

}