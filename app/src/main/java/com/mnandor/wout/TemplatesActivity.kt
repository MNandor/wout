package com.mnandor.wout

import ConfigViewModel
import ConfigViewModelFactory
import TemplatesViewModel
import TemplatesViewModelFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mnandor.wout.databinding.ActivityMainBinding
import com.mnandor.wout.databinding.ActivityTemplatesBinding

class TemplatesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTemplatesBinding
    private var templates: List<ExerciseTemplate>? = null
    private var currentExercise: ExerciseTemplate? = null

    private val templatesViewModel: TemplatesViewModel by viewModels {
        TemplatesViewModelFactory((application as WoutApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityTemplatesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        templatesViewModel.allVisibleTemplates.observe(this, Observer { items ->
            val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item, items.map { it->it.name }
            )

            binding.exerciseDropdown2.adapter = adapter

            adapter.notifyDataSetChanged()

            templates = items

        })

        val recyclerView = binding.templatesRecycle
        val adapter = TemplateItemsAdapter()
        //adapter.setDeleteCallback { deleteExerciseLog(it) }
        //adapter.setEditCallback { editExerciseLog(it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        templatesViewModel.allTemplateItems.observe(this, Observer { items ->
            items?.let{adapter.setItems(items)}
            adapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(items.size-1)
        })

        setClickListeners()
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
            templatesViewModel.insert(TemplateItem(0, currentExercise!!.name, binding.editTextTextPersonName.text.toString()))
        }

    }
}