package com.mnandor.wout.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mnandor.wout.R
import com.mnandor.wout.data.entities.Exercise

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private var items:List<String> = listOf()

    fun setItems(newItems:List<String>){
        items = newItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return ScheduleViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val current = items[position]
        holder.bind(current)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ScheduleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val scheduleDate: TextView = itemView.findViewById(R.id.scheduleDate)
        private val iconToday: ImageView = itemView.findViewById(R.id.iconToday)

        fun bind(item: String) {
            if (item.startsWith('+')){
                scheduleDate.text=item.removePrefix("+")
                iconToday.visibility=View.VISIBLE
            } else{
                scheduleDate.text = item
                iconToday.visibility=View.INVISIBLE
            }

        }

        companion object {
            fun create(parent: ViewGroup): ScheduleViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_schedule_day, parent, false)
                return ScheduleViewHolder(view)
            }
        }
    }

}