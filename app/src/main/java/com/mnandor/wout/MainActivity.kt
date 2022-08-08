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
                R.layout.simple_spinner_dropdown_item, items.map { it->it.name }
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

    private var selectedItem:ExerciseTemplate? = null

    private fun updateUIAfterDropdown(item: ExerciseTemplate?){
        if (item == null)
            return

        selectedItem = item

        binding.timeET.visibility = if (item!!.usesTime) View.VISIBLE else View.GONE
        binding.distanceET.visibility = if (item!!.usesDistance) View.VISIBLE else View.GONE
        binding.weightET.visibility = if (item!!.usesWeight) View.VISIBLE else View.GONE
        binding.setCountET.visibility = if (item!!.usesSetCount) View.VISIBLE else View.GONE
        binding.repCountET.visibility = if (item!!.usesRepCount) View.VISIBLE else View.GONE

        binding.timeET.text.clear()
        binding.distanceET.text.clear()
        binding.weightET.text.clear()
        binding.setCountET.text.clear()
        binding.repCountET.text.clear()

    }

    private fun addExerciseLog(){
        if (selectedItem == null)
            return

        if (templates?.size == 0) {
            openConfigActivity()
            return
        }
        Toast.makeText(this,
            templates?.get(binding.exerciseDropdown.selectedItemPosition)?.name ?: "Long click on Add first", Toast.LENGTH_SHORT
        ).show()

        mainViewModel.insert(ExerciseLog(
            "hi",
            binding.exerciseDropdown.selectedItem.toString(),
            if(selectedItem!!.usesTime) binding.timeET.text.toString() else null,
            if(selectedItem!!.usesDistance) binding.distanceET.text.toString().toFloatOrNull() else null,
            if(selectedItem!!.usesWeight) binding.weightET.text.toString().toFloatOrNull() else null,
            if(selectedItem!!.usesSetCount) binding.setCountET.text.toString().toIntOrNull() else null,
            if(selectedItem!!.usesRepCount) binding.repCountET.text.toString().toIntOrNull() else null,

        ))

    }

    private fun openConfigActivity(){
        val intent = Intent(this, ConfigActivity::class.java)
        startActivity(intent)
    }
}