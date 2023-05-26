package com.mnandor.wout.presentation.exercises

import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mnandor.wout.WoutApplication
import com.mnandor.wout.data.entities.Completion
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.databinding.ActivityConfigBinding
import com.mnandor.wout.databinding.DialogEditExerciseBinding
import com.mnandor.wout.databinding.DialogEditLogBinding
import com.mnandor.wout.presentation.EditExerciseDialog

class ExercisesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding

    private val viewModel: ExercisesViewModel by viewModels {
        ExercisesViewModelFactory((application as WoutApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfigBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val recyclerView = binding.exerciseTemplatesRecycle
        val adapter = ExercisesRecyclerAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.allTemplates.observe(this, Observer { items ->
            items?.let{adapter.setItems(items)}
            adapter.notifyDataSetChanged()
        })

        adapter.setEditCallback {
            editExerciseName(it)
        }

        adapter.setDeleteCallback {
            deleteExerciseLog(it)
        }


        setClickListeners()
    }

    public fun editExerciseName(exercise: Exercise){

        val dialog = EditExerciseDialog(this)

        dialog.setRenameCallback{ oldName, newName ->
            viewModel.rename(oldName, newName)
        }

        dialog.setUpdateCallback {
            viewModel.updateExercise(it)
        }

        dialog.setExercise(exercise)

        dialog.show()
    }

    public fun deleteExerciseLog(exercise: Exercise){
        AlertDialog.Builder(this)
            .setTitle(exercise.name)
            .setMessage("Do you really want to delete this exercise type?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, whichButton ->
                    viewModel.delete(exercise)
                })
            .setNegativeButton(android.R.string.no, null).show()
    }

    private fun setClickListeners(){
        binding.fab.setOnClickListener {
            val dialog = EditExerciseDialog(this)

            dialog.setUpdateCallback {
                viewModel.insert(it)
            }

            dialog.setCreateMode()

            dialog.show()
        }

        val switchTime = binding.switchTime
        val switchDist = binding.switchDist
        val switchMass = binding.switchMass
        val switchReps = binding.switchReps
        val switchSets = binding.switchSets

        val exNameET = binding.newExTemplateET


        binding.button.setOnClickListener {
            viewModel.insert(
                Exercise(
                exNameET.text.toString(),
                switchTime.isChecked,
                switchDist.isChecked,
                switchMass.isChecked,
                switchSets.isChecked,
                switchReps.isChecked,
                false,
                    null
            )
            )
        }

        binding.button.setOnLongClickListener {
            return@setOnLongClickListener true // yes, consume event
        }

    }


}