package com.mnandor.wout.presentation.schedule

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.mnandor.wout.R
import com.mnandor.wout.data.entities.Location
import com.mnandor.wout.data.entities.ScheduleDay

class ScheduleRecyclerAdapter : RecyclerView.Adapter<ScheduleRecyclerAdapter.ScheduleViewHolder>() {

    private var items:List<String> = listOf()
    private var dropDownOptions:List<Location> = listOf()
    private var dropDownValues:List<ScheduleDay> = listOf()

    fun setItems(newItems:List<String>){
        items = newItems
    }

    public fun getMap():Map<Int, String>{
        return ScheduleViewHolder.userSetSchedules
    }

    fun setDropDownOptions(options: List<Location>){

        val itemsMut = options.toMutableList()

        itemsMut.add(Location(-1, "", "All"))

        dropDownOptions = itemsMut


    }

    fun setDropdownValues(values: List<ScheduleDay>){
        dropDownValues = values
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return ScheduleViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val current = items[position]

        var strToSend = "All"
        if (dropDownValues.any { it.day == position }){
            val selection:ScheduleDay = dropDownValues.first { it.day == position }
            strToSend = dropDownOptions.find { selection.location == it.itemID }?.template ?: "All"

        }

        holder.bind(current, dropDownOptions, strToSend, position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ScheduleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val scheduleDate: TextView = itemView.findViewById(R.id.scheduleDate)
        private val iconToday: ImageView = itemView.findViewById(R.id.iconToday)
        private val dropDown: Spinner = itemView.findViewById(R.id.scheduleLocationSelector)

        fun bind(item: String, locations:List<Location>, thisOption: String, thisDayNumber: Int) {
            val options = locations.map { it.template }

            if (item.startsWith('+')){
                scheduleDate.text=item.removePrefix("+")
                iconToday.visibility=View.VISIBLE
            } else{
                scheduleDate.text = item
                iconToday.visibility=View.INVISIBLE
            }

            val adapter = ArrayAdapter<String>(
                itemView.context,
                R.layout.spinner_item,
                options
            )

            dropDown.adapter = adapter

            val index = options.indexOf(thisOption)
            if (index != -1) {
                dropDown.setSelection(index)
            }

            dropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long){
                    userSetSchedules[thisDayNumber] = dropDown.selectedItem.toString()
                }
            }

        }

        companion object {
            fun create(parent: ViewGroup): ScheduleViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_schedule_day, parent, false)
                return ScheduleViewHolder(view)
            }

            public val userSetSchedules = mutableMapOf<Int, String>()
        }
    }

}