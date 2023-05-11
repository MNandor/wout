package com.mnandor.wout.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mnandor.wout.WoutApplication
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.databinding.ActivityConfigBinding

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding

    private val configViewModel: ConfigViewModel by viewModels {
        ConfigViewModelFactory((application as WoutApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfigBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val recyclerView = binding.exerciseTemplatesRecycle
        val adapter = ExerciseTemplatesAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        configViewModel.allTemplates.observe(this, Observer { items ->
            items?.let{adapter.setItems(items)}
            adapter.notifyDataSetChanged()
        })

        setClickListeners()
    }

    private fun setClickListeners(){
        val switchTime = binding.switchTime
        val switchDist = binding.switchDist
        val switchMass = binding.switchMass
        val switchReps = binding.switchReps
        val switchSets = binding.switchSets

        val exNameET = binding.newExTemplateET


        binding.button.setOnClickListener {
            configViewModel.insert(
                Exercise(
                exNameET.text.toString(),
                switchTime.isChecked,
                switchDist.isChecked,
                switchMass.isChecked,
                switchSets.isChecked,
                switchReps.isChecked,
                false
            )
            )
        }

        binding.button.setOnLongClickListener {
            return@setOnLongClickListener true // yes, consume event
        }

    }


}