package com.mnandor.wout.presentation

import android.app.Dialog
import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.View
import com.mnandor.wout.data.entities.Completion
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.databinding.DialogEditExerciseBinding
import com.mnandor.wout.databinding.DialogEditLogBinding

class EditCompletionDialog(context: Context) : Dialog(context) {
    private val binding: DialogEditLogBinding
    init {
        binding = DialogEditLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.dialogCancelButton.setOnClickListener {
            dismiss()
        }
    }
    fun setCompletion(completion: Completion){
        with(binding){
            dialogLogName.text = completion.exercise
            dialogLogDateET.setText(completion.timestamp.split(" ")[0])
            dialogLogTimeET.setText(completion.timestamp.split(" ")[1])

            if (completion.duration != null) dialogTimeET.setText(completion.duration) else dialogTimeET.visibility = View.GONE
            if (completion.distance != null) dialogDistanceET.setText(completion.distance.toString()) else dialogDistanceET.visibility = View.GONE
            if (completion.weight != null) dialogWeightET.setText(completion.weight.toString()) else dialogWeightET.visibility = View.GONE
            if (completion.sets != null) dialogSetCountET.setText(completion.sets.toString()) else dialogSetCountET.visibility = View.GONE
            if (completion.reps != null) dialogRepCountET.setText(completion.reps.toString()) else dialogRepCountET.visibility = View.GONE

            dialogCancelButton.setOnClickListener { dismiss() }
            dialogChangeButton.setOnClickListener {
                // todo changing timestamp is not supported as it's a primary key

                val newDate = dialogLogDateET.text.toString()+" "+dialogLogTimeET.text.toString()
                if (newDate != completion.timestamp && redateCallback != null){
                    redateCallback!!(completion.timestamp, newDate)
                }


                var duration:String? = dialogTimeET.text.toString()
                if (duration.isNullOrEmpty())
                    duration = null
                val newLog: Completion = Completion(
                    timestamp = newDate,
                    exercise = completion.exercise,
                    duration = duration,
                    distance = dialogDistanceET.text.toString().toFloatOrNull(),
                    weight = dialogWeightET.text.toString().toFloatOrNull(),
                    sets = dialogSetCountET.text.toString().toIntOrNull(),
                    reps = dialogRepCountET.text.toString().toIntOrNull(),
                )

                updateCallback?.let { it1 -> it1(newLog) }
                dismiss()

            }
        }

    }


    private var redateCallback: ((String, String) -> Unit)? = null
    public fun setRedateCallback(callback: (oldDate: String, newDate: String) -> Unit){
        redateCallback = callback
    }

    private var updateCallback: ((Completion) -> Unit)? = null
    public fun setUpdateCallback(callback: (Completion) -> Unit){
        updateCallback = callback
    }


}