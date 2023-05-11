package com.mnandor.wout.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mnandor.wout.R
import com.mnandor.wout.data.entities.Location
import java.time.LocalDate

class LocationsAdapter : RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder>() {

    companion object{
        val TYPE_TEMPLATE = 0
        val TYPE_EXERCISE = 1
    }


    private var items:List<Location> = listOf()
    private lateinit var deleteCallback: (Location) -> Unit
    private lateinit var editCallback: (Location) -> Unit

    fun setDeleteCallback(callback: (Location) -> Unit){
        deleteCallback = callback
    }

    fun setEditCallback(callback: (Location) -> Unit){
        editCallback = callback
    }

    fun callDeleteCallback(item: Location){
        deleteCallback(item)
    }

    fun callEditCallback(item: Location){
        editCallback(item)
    }


    private val holeyItems:ArrayList<Location?> = ArrayList()
    private val holeyDates:ArrayList<LocalDate?> = ArrayList()
    fun setItems(newItems:List<Location>){
        items = newItems
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_template, parent, false)
        return LocationsViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        val current = items[position]!!
        holder.bind(current)
        holder.itemView.setOnLongClickListener{
            callDeleteCallback(current)
            return@setOnLongClickListener true
        }
        holder.itemView.setOnClickListener {
            callEditCallback(current)
        }

    }


    override fun getItemCount(): Int {
        return items.size
    }

    class LocationsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bind(item: Location) {
            itemView.findViewById<TextView>(R.id.scheduleDate).text = item.template
            itemView.findViewById<TextView>(R.id.dayTemplateExercise).text = item.exercise

        }

    }
}