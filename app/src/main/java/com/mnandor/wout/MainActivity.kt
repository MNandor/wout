package com.mnandor.wout

import MainViewModel
import MainViewModelFactory
import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mnandor.wout.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var templates: List<ExerciseTemplate>? = null

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

            templates = items
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

        binding.exerciseDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long){
                if (templates?.size != 0)
                    updateUIAfterDropdown(templates?.get(binding.exerciseDropdown.selectedItemPosition))
            }
        }
    }

    private fun updateUIAfterDropdown(item: ExerciseTemplate?){
        if (item == null)
            return
        
        binding.timeET.visibility = if (item!!.usesTime) View.VISIBLE else View.GONE
        binding.distanceET.visibility = if (item!!.usesDistance) View.VISIBLE else View.GONE
        binding.weightET.visibility = if (item!!.usesWeight) View.VISIBLE else View.GONE
        binding.setCountET.visibility = if (item!!.usesSetCount) View.VISIBLE else View.GONE
        binding.repCountET.visibility = if (item!!.usesRepCount) View.VISIBLE else View.GONE

    }

    private fun addExerciseLog(){
        if (templates?.size == 0) {
            openConfigActivity()
            return;
        }
        Toast.makeText(this,
            templates?.get(binding.exerciseDropdown.selectedItemPosition)?.name ?: "Long click on Add first", Toast.LENGTH_SHORT
        ).show()

    }

    private fun openConfigActivity(){
        val intent = Intent(this, ConfigActivity::class.java)
        startActivity(intent)
    }
}