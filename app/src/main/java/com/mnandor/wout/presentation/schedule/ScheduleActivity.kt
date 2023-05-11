package com.mnandor.wout.presentation.schedule

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.mnandor.wout.WoutApplication
import com.mnandor.wout.databinding.ActivityScheduleBinding
import java.text.SimpleDateFormat
import java.util.*

class ScheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleBinding
    private lateinit var adapter: ScheduleAdapter


    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as WoutApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScheduleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val recyclerView = binding.scheduleRecycler
        adapter = ScheduleAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.scheduleTotalET.addTextChangedListener { updateRecycler() }
        binding.scheduleTodayET.addTextChangedListener { updateRecycler() }

        scheduleViewModel.scheduleTotal.observe(this, androidx.lifecycle.Observer {
            binding.scheduleTotalET.setText(it)
        })

        scheduleViewModel.scheduleOffset.observe(this, androidx.lifecycle.Observer {
            binding.scheduleTodayET.setText(it)
        })

        scheduleViewModel.getValuesFromDB()

        binding.scheduleConfirmButton.setOnClickListener {
            scheduleViewModel.setLoopAndOffset(totalDays, offset)
        }

    }

    private fun getScheduledDates(totalDays: Int, todayIs: Int): MutableList<String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd, EEEE", Locale.getDefault())
        calendar.add(Calendar.DATE, -todayIs)

        val list = mutableListOf<String>()

        for (i in 1..totalDays){
            calendar.add(Calendar.DATE, 1)
            var date = dateFormat.format(calendar.time)

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
        offset = today

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