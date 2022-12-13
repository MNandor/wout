package com.mnandor.wout

import ConfigViewModel
import ConfigViewModelFactory
import TemplatesViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TemplatesActivity : AppCompatActivity() {
    private val templatesViewModel: TemplatesViewModel by viewModels {
        ConfigViewModelFactory((application as WoutApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_templates)
        setClickListeners()
    }

    private fun setClickListeners(){

    }
}