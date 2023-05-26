package com.mnandor.wout.presentation

import android.app.Dialog
import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.View
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.databinding.DialogEditExerciseBinding

class EditExerciseDialog(context: Context) : Dialog(context) {
    private val binding: DialogEditExerciseBinding
    init {
        binding = DialogEditExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.dialogCancelButton.setOnClickListener {
            dismiss()
        }
    }
    fun setExercise(exercise: Exercise){
        with(binding){
            dialogLogName.text = exercise.name
            dialogLogDateET.setText(exercise.name)
            dialogExerciseNotesET.setText(exercise.notes?:"")
            usesTime.isChecked = exercise.usesTime
            usesMass.isChecked = exercise.usesWeight
            usesDistance.isChecked = exercise.usesDistance
            usesSets.isChecked = exercise.usesSetCount
            usesReps.isChecked = exercise.usesRepCount


            dialogChangeButton.setOnClickListener {

                val oldName = exercise.name
                val newName = dialogLogDateET.text.toString()

                if (oldName != newName && renameCallback != null)
                    renameCallback!!(oldName, newName)

                if (updateCallback != null){
                    val newex = Exercise(
                        name = newName,
                        usesTime = usesTime.isChecked,
                        usesDistance = usesDistance.isChecked,
                        usesWeight = usesMass.isChecked,
                        usesSetCount = usesSets.isChecked,
                        usesRepCount = usesReps.isChecked,
                        isDisabled = false,
                        notes = dialogExerciseNotesET.text.toString()
                    )

                    updateCallback!!(newex)
                }

                dismiss()
            }


        }
    }

    fun setCreateMode(){
        with(binding){
            dialogChangeButton.setOnClickListener {
                val newName = dialogLogDateET.text.toString()

                if (updateCallback != null){
                    val newex = Exercise(
                        name = newName,
                        usesTime = usesTime.isChecked,
                        usesDistance = usesDistance.isChecked,
                        usesWeight = usesMass.isChecked,
                        usesSetCount = usesSets.isChecked,
                        usesRepCount = usesReps.isChecked,
                        isDisabled = false,
                        notes = dialogExerciseNotesET.text.toString()
                    )

                    updateCallback!!(newex)
                }
                dismiss()
            }

            dialogChangeButton.setText("Add")
            textView.setText("Add Exercise")
            dialogLogName.visibility= View.GONE
        }

    }

    private var renameCallback: ((String, String) -> Unit)? = null
    public fun setRenameCallback(callback: (oldName: String, newName: String) -> Unit){
        renameCallback = callback
    }

    private var updateCallback: ((Exercise) -> Unit)? = null
    public fun setUpdateCallback(callback: (Exercise) -> Unit){
        updateCallback = callback
    }


}