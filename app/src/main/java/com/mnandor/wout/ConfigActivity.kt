package com.mnandor.wout

import ConfigViewModel
import ConfigViewModelFactory
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mnandor.wout.databinding.ActivityConfigBinding
import com.mnandor.wout.databinding.ActivityMainBinding

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


        binding.button.setOnClickListener {
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

        binding.button.setOnLongClickListener {
            openTemplatesActivity()
            return@setOnLongClickListener true // yes, consume event
        }

    }

    private fun openTemplatesActivity(){
        val intent = Intent(this, TemplatesActivity::class.java)
        startActivity(intent)
    }
}