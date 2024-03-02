package com.mnandor.wout.presentation.graph

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
import com.mnandor.wout.databinding.ActivityGraphBinding
import com.mnandor.wout.databinding.DialogEditExerciseBinding
import com.mnandor.wout.databinding.DialogEditLogBinding
import com.mnandor.wout.presentation.EditExerciseDialog

class GraphActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGraphBinding

    private val viewModel: GraphViewModel by viewModels {
        GraphViewModelFactory((application as WoutApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGraphBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val exerciseName:String = intent.extras?.get("exerciseName").toString()

        binding.graphExerciseName.text = exerciseName


        setClickListeners()
    }


    private fun setClickListeners(){

    }


}