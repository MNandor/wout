package com.mnandor.wout.presentation

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.mnandor.wout.R
import com.mnandor.wout.databinding.ViewTodayIsSelectorBinding

class TodayIsSelector @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding:ViewTodayIsSelectorBinding
    init {
        binding= ViewTodayIsSelectorBinding.inflate(LayoutInflater.from(context), this, true)


        binding.dayTemplateSelector2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //Toast.makeText(this@MainActivity, "*", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long){
                if (dayTemplates?.size != 0){
                    val item = dayTemplates?.get(binding.dayTemplateSelector2.selectedItemPosition)
                    if (item != null)
                        callbackFunction?.let { it(item) }
                }

            }
        }
    }


    private var dayTemplates: List<String>? = null
    public fun setItems(items:List<String>){
        val itemsMut = items.toMutableList()
        binding.dayTemplateSelector2.isEnabled = items.isNotEmpty()

        itemsMut.add("All")

        val adapter = ArrayAdapter<String>(
            context,
            R.layout.spinner_item,
            itemsMut
        )

        binding.dayTemplateSelector2.adapter = adapter

        adapter.notifyDataSetChanged()

        dayTemplates = itemsMut
    }

    public fun setSelection(item: String){
        dayTemplates?.let { it1 -> binding.dayTemplateSelector2.setSelection(it1.indexOf(item)) }
    }

    private var callbackFunction: ((String) -> Unit)? = null
    public fun setCallback(callback: (String) -> Unit){
        callbackFunction = callback
    }

}