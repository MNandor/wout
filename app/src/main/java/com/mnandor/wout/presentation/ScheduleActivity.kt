package com.mnandor.wout.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mnandor.wout.WoutApplication
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.databinding.ActivityConfigBinding
import com.mnandor.wout.databinding.ActivityScheduleBinding

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
    }

}