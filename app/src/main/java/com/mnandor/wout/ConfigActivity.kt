package com.mnandor.wout

import ConfigViewModel
import ConfigViewModelFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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

        findViewById<Button>(R.id.button).setOnClickListener {
            configViewModel.insert(ExerciseTemplate("Hello"+(0..999).random().toString(), false, false, false, false, false, false))
        }
    }
}