package com.mnandor.wout.presentation.exercises

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mnandor.wout.R
import com.mnandor.wout.data.entities.Completion
import com.mnandor.wout.data.entities.Exercise

class ExercisesRecyclerAdapter : RecyclerView.Adapter<ExercisesRecyclerAdapter.ExerciseTemplateViewHolder>() {

    private var items:List<Exercise> = listOf()

    private lateinit var editCallback: (Exercise) -> Unit
    private lateinit var deleteCallback: (Exercise) -> Unit
    private lateinit var openGraphCallback: (Exercise) -> Unit

    fun setEditCallback(callback: (Exercise) -> Unit){
        editCallback = callback
    }

    fun setDeleteCallback(callback: (Exercise) -> Unit){
        deleteCallback = callback
    }

    fun setOpenGraphCallback(callback: (Exercise) -> Unit){
        openGraphCallback = callback
    }

    fun setItems(newItems:List<Exercise>){
        items = newItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseTemplateViewHolder {
        return ExerciseTemplateViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ExerciseTemplateViewHolder, position: Int) {
        val current = items[position]
        holder.bind(current)
        holder.itemView.setOnLongClickListener{
            deleteCallback(current)
            return@setOnLongClickListener true
        }
        holder.itemView.setOnClickListener {
            editCallback(current)
        }
        holder.itemView.findViewById<ImageButton>(R.id.openGraphButton).setOnClickListener{
            openGraphCallback(current)
        }
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