package com.mnandor.wout.presentation

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mnandor.wout.WoutApplication
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.databinding.ActivityConfigBinding
import com.mnandor.wout.databinding.ActivityScheduleBinding
import java.text.SimpleDateFormat
import java.util.*

class ScheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleBinding

    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory((application as WoutApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScheduleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val recyclerView = binding.scheduleRecycler
        val adapter = ScheduleAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        updateViewModel(7, 3)
    }

    private fun updateViewModel(totalDays: Int, todayIs: Int) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        calendar.add(Calendar.DATE, -todayIs)

        for (i in 1..totalDays){
            calendar.add(Calendar.DATE, 1)
            val date = dateFormat.format(calendar.time)

            Log.d("in-dev", date)

            if (i == todayIs)
                Log.d("in-dev", date)
        }

    }

}