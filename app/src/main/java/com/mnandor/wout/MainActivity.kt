package com.mnandor.wout

import MainViewModel
import MainViewModelFactory
import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mnandor.wout.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as WoutApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setClickListeners()


        mainViewModel.allVisibleTemplates.observe(this, Observer { items ->
            val adapter = ArrayAdapter<String>(
                this,
                R.layout.simple_spinner_item, items.map { it->it.name }
            )

            binding.exerciseDropdown.adapter = adapter

            adapter.notifyDataSetChanged()
        })
    }

    private fun setClickListeners(){
        binding.addButton.setOnClickListener {
            addExerciseLog()
        }

        binding.addButton.setOnLongClickListener {
            openConfigActivity()
            return@setOnLongClickListener true // yes, consume event
        }
    }

    private fun addExerciseLog(){

    }

    private fun openConfigActivity(){
        val intent = Intent(this, ConfigActivity::class.java)
        startActivity(intent)
    }
}