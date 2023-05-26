package com.mnandor.wout.presentation.schedule

import android.icu.util.Calendar
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.mnandor.wout.DateUtility
import com.mnandor.wout.DateUtility.Companion.offsetModifier
import com.mnandor.wout.R
import com.mnandor.wout.WoutApplication
import com.mnandor.wout.data.entities.Location
import com.mnandor.wout.databinding.ActivityScheduleBinding
import java.util.*
import java.util.concurrent.TimeUnit

class ScheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleBinding
    private lateinit var adapter: ScheduleRecyclerAdapter


    private val viewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as WoutApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScheduleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val recyclerView = binding.scheduleRecycler
        adapter = ScheduleRecyclerAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.scheduleTotalET.addTextChangedListener { updateRecycler() }
        binding.scheduleTodayET.addTextChangedListener { updateRecycler() }


        viewModel.schedule.observe(this, androidx.lifecycle.Observer {
            var total = it.first
            if (total.isNullOrEmpty())
                total = "1"
            totalDays = total.toInt()
            binding.scheduleTotalET.setText(total)
            var offset = it.second
            if (offset.isNullOrEmpty())
                offset = "1"
            var value = (offset.toInt()+offsetModifier(totalDays)+totalDays).toInt()%totalDays
            if (value == 0)
                value = totalDays
            binding.scheduleTodayET.setText(value.toString())
        })

        viewModel.getValuesFromDB()

        binding.scheduleConfirmButton.setOnClickListener {
            viewModel.setLoopAndOffset(totalDays, offset)
            val map = adapter.getMap()
            map.forEach {
                if (it.value != "All")
                    viewModel.updateScheduleDay(it.key, it.value)
                else{
                    viewModel.removeScheduleDayData(it.key)
                }
            }

            Toast.makeText(this, "Schedule Saved!", Toast.LENGTH_SHORT).show()
        }

        viewModel.allDayTemplates.observe(this, androidx.lifecycle.Observer {
            adapter.setDropDownOptions(it)
            adapter.notifyDataSetChanged()

        })

        viewModel.allDayTemplateNames.observe(this, androidx.lifecycle.Observer { items ->
            binding.todayIsSelector.setItems(items)

            viewModel.loadLocationSetting()
        })

        viewModel.locationSetting.observe(this, androidx.lifecycle.Observer {
            binding.todayIsSelector.setSelection(it)
        })

        binding.todayIsSelector.setCallback {
            viewModel.setFilter(it)
        }

        viewModel.allScheduleDays.observe(this, androidx.lifecycle.Observer {
            adapter.setDropdownValues(it)
            adapter.notifyDataSetChanged()
        })

    }

    private var dayTemplates:List<String>? = null

    private fun getScheduledDates(totalDays: Int, todayIs: Int): MutableList<String> {
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.DATE, -todayIs)

        val list = mutableListOf<String>()

        for (i in 1..totalDays){
            calendar.add(Calendar.DATE, 1)
            var date = DateUtility.dateFormat.format(calendar.time)

            if (i == todayIs)
                date = "+"+date

            list.add(date)
        }

        return list

    }


    private var totalDays:Int = 0
    private var offset:Int = 0

    private fun updateRecycler(){
        val totalET = binding.scheduleTotalET
        val todayET = binding.scheduleTodayET

        val today = todayET.text.toString().toIntOrNull()
        if (today == null){
            return
        }
        updateCardinal(today)

        val total = totalET.text.toString().toIntOrNull()
        if (total == null){
            return
        }

        val days = getScheduledDates(total, today)

        totalDays = total
        offset = (today-offsetModifier(totalDays)).toInt()%total

        adapter.setItems(days)

        adapter.notifyDataSetChanged()

    }

    private fun updateCardinal(num: Int){
        val cardinal = binding.scheduleCardinal
        val rem = num%10;

        if (rem == 1) cardinal.text = "st."
        else if (rem == 2) cardinal.text = "nd."
        else if (rem == 3) cardinal.text = "rd."
        else cardinal.text = "th."

    }

}