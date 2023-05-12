package com.mnandor.wout.presentation.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mnandor.wout.R

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private var items:List<String> = listOf()
    private var dropDownOptions:List<String> = listOf()
    private var dropDownValues:List<String> = listOf()

    fun setItems(newItems:List<String>){
        items = newItems
    }

    fun setDropDownOptions(options: List<String>){
        dropDownOptions = options
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return ScheduleViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val current = items[position]
        holder.bind(current, dropDownOptions)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ScheduleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val scheduleDate: TextView = itemView.findViewById(R.id.scheduleDate)
        private val iconToday: ImageView = itemView.findViewById(R.id.iconToday)
        private val dropDown: Spinner = itemView.findViewById(R.id.scheduleLocationSelector)

        fun bind(item: String, options:List<String>) {
            if (item.startsWith('+')){
                scheduleDate.text=item.removePrefix("+")
                iconToday.visibility=View.VISIBLE
            } else{
                scheduleDate.text = item
                iconToday.visibility=View.INVISIBLE
            }

            val adapter = ArrayAdapter<String>(
                itemView.context,
                android.R.layout.simple_spinner_dropdown_item, options
            )

            dropDown.adapter = adapter

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