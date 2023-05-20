package com.mnandor.wout.presentation.locations

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mnandor.wout.R
import com.mnandor.wout.WoutApplication
import com.mnandor.wout.data.entities.Completion
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.data.entities.Location
import com.mnandor.wout.databinding.ActivityTemplatesBinding

class LocationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTemplatesBinding
    private var templates: List<Exercise>? = null
    private var currentExercise: Exercise? = null

    private val viewModel: LocationsViewModel by viewModels {
        LocationsViewModelFactory((application as WoutApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityTemplatesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        viewModel.allVisibleTemplates.observe(this, Observer { items ->
            val adapter = ArrayAdapter<String>(
                this,
                R.layout.spinner_item,
                items.map { it->it.name }
            )

            binding.exerciseDropdown2.adapter = adapter

            adapter.notifyDataSetChanged()

            templates = items

        })

        val recyclerView = binding.templatesRecycle
        val adapter = LocationsRecyclerAdapter()
        adapter.setDeleteCallback { deleteLocation(it) }
        //adapter.setEditCallback { editExerciseLog(it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        viewModel.allLocations.observe(this, Observer { items ->
            items?.let{adapter.setItems(items)}
            adapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(items.size-1)
        })

        setClickListeners()
    }

    public fun deleteLocation(location: Location){
        AlertDialog.Builder(this)
            .setTitle(location.template+" - "+location.exercise)
            .setMessage("Do you really want to delete this?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, whichButton ->
                    viewModel.remove(location)
                })
            .setNegativeButton(android.R.string.no, null).show()
    }

    private fun setClickListeners(){
        binding.exerciseDropdown2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long){
                if (templates?.size != 0)
                    currentExercise = templates?.get(binding.exerciseDropdown2.selectedItemPosition)
            }
        }

        binding.button2.setOnClickListener {
            viewModel.insert(Location(0, currentExercise!!.name, binding.editTextTextPersonName.text.toString()))
        }

    }
}