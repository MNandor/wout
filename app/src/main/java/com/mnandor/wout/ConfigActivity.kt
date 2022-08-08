package com.mnandor.wout

import ConfigViewModel
import ConfigViewModelFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ConfigActivity : AppCompatActivity() {
    private val configViewModel: ConfigViewModel by viewModels {
        ConfigViewModelFactory((application as WoutApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        val recyclerView = findViewById<RecyclerView>(R.id.exerciseTemplatesRecycle)
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
        val switchTime = findViewById<Switch>(R.id.switchTime)
        val switchDist = findViewById<Switch>(R.id.switchDist)
        val switchMass = findViewById<Switch>(R.id.switchMass)
        val switchReps = findViewById<Switch>(R.id.switchReps)
        val switchSets = findViewById<Switch>(R.id.switchSets)

        val exNameET = findViewById<EditText>(R.id.newExTemplateET)


        findViewById<Button>(R.id.button).setOnClickListener {
            configViewModel.insert(ExerciseTemplate(
                exNameET.text.toString(),
                switchTime.isChecked,
                switchDist.isChecked,
                switchMass.isChecked,
                switchSets.isChecked,
                switchReps.isChecked,
                false
            ))
        }

    }
}