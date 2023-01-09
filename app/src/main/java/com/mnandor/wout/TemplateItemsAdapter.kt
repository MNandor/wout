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

class TemplateItemsAdapter : RecyclerView.Adapter<TemplateItemsAdapter.TemplateItemsViewHolder>() {

    companion object{
        val TYPE_TEMPLATE = 0
        val TYPE_EXERCISE = 1
    }


    private var items:List<TemplateItem> = listOf()
    private lateinit var deleteCallback: (TemplateItem) -> Unit
    private lateinit var editCallback: (TemplateItem) -> Unit

    fun setDeleteCallback(callback: (TemplateItem) -> Unit){
        deleteCallback = callback
    }

    fun setEditCallback(callback: (TemplateItem) -> Unit){
        editCallback = callback
    }

    fun callDeleteCallback(item: TemplateItem){
        deleteCallback(item)
    }

    fun callEditCallback(item: TemplateItem){
        editCallback(item)
    }


    private val holeyItems:ArrayList<TemplateItem?> = ArrayList()
    private val holeyDates:ArrayList<LocalDate?> = ArrayList()
    fun setItems(newItems:List<TemplateItem>){
        items = newItems
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateItemsViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_template, parent, false)
        return TemplateItemsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TemplateItemsViewHolder, position: Int) {
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

    class TemplateItemsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bind(item: TemplateItem) {
            itemView.findViewById<TextView>(R.id.dayTemplateName).text = item.template
            itemView.findViewById<TextView>(R.id.dayTemplateExercise).text = item.exercise

        }

    }
}