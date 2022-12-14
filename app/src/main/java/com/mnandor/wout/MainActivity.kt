package com.mnandor.wout

import MainViewModel
import MainViewModelFactory
import android.R
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mnandor.wout.databinding.ActivityMainBinding
import com.mnandor.wout.databinding.DialogEditLogBinding
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var templates: List<ExerciseTemplate>? = null
    private var dayTemplates: List<String>? = null

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

        mainViewModel.trendlinePrediction.observe(this, Observer{
            binding.repCountET.setHint("/"+it.toString())
        })


        val recyclerView = binding.exerciseLogsRecycle
        val adapter = ExerciseLogsAdapter()
        adapter.setDeleteCallback { deleteExerciseLog(it) }
        adapter.setEditCallback { editExerciseLog(it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        mainViewModel.allLogs.observe(this, Observer { items ->
            items?.let{adapter.setItems(items)}
            adapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(items.size-1)
        })

        mainViewModel.allDayTemplates.observe(this, Observer { items ->
            val itemsMut = items.toMutableList()

            binding.dayTemplateSelector.isEnabled = items.isNotEmpty()

            itemsMut.add("All")

            val adapter = ArrayAdapter<String>(
                this,
                R.layout.simple_spinner_dropdown_item, itemsMut
            )

            binding.dayTemplateSelector.adapter = adapter

            adapter.notifyDataSetChanged()

            dayTemplates = itemsMut
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

        mainViewModel.setFilter("All")
        binding.dayTemplateSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //Toast.makeText(this@MainActivity, "*", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long){
                if (dayTemplates?.size != 0)
                    mainViewModel.setFilter(dayTemplates?.get(binding.dayTemplateSelector.selectedItemPosition)!!)
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

        if (item!!.usesRepCount){
            Toast.makeText(this, "toasty", Toast.LENGTH_SHORT).show()
            mainViewModel.calculateTrendline(item)
        }

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

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentTime = sdf.format(Calendar.getInstance().time)

        mainViewModel.insert(ExerciseLog(
            currentTime,
            binding.exerciseDropdown.selectedItem.toString(),
            if(selectedItem!!.usesTime and binding.timeET.text.toString().isNotEmpty()) binding.timeET.text.toString() else null,
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

    public fun deleteExerciseLog(exerciseLog: ExerciseLog){
        AlertDialog.Builder(this)
            .setTitle(exerciseLog.exercise)
            .setMessage("Do you really want to empty the exercise log?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, whichButton ->
                    mainViewModel.deleteExerciseLog(exerciseLog)
                })
            .setNegativeButton(android.R.string.no, null).show()
    }

    public fun editExerciseLog(exerciseLog: ExerciseLog){
        Toast.makeText(this, exerciseLog.exercise, Toast.LENGTH_SHORT).show()
        val settingsDialog = Dialog(this)

        val dialogBinding = DialogEditLogBinding.inflate(layoutInflater)

        with(dialogBinding){
            dialogLogName.text = exerciseLog.exercise
            dialogLogDateET.setText(exerciseLog.timestamp.split(" ")[0])
            dialogLogTimeET.setText(exerciseLog.timestamp.split(" ")[1])

            if (exerciseLog.duration != null) dialogTimeET.setText(exerciseLog.duration) else dialogTimeET.visibility = View.GONE
            if (exerciseLog.distance != null) dialogDistanceET.setText(exerciseLog.distance.toString()) else dialogDistanceET.visibility = View.GONE
            if (exerciseLog.weight != null) dialogWeightET.setText(exerciseLog.weight.toString()) else dialogWeightET.visibility = View.GONE
            if (exerciseLog.sets != null) dialogSetCountET.setText(exerciseLog.sets.toString()) else dialogSetCountET.visibility = View.GONE
            if (exerciseLog.reps != null) dialogRepCountET.setText(exerciseLog.reps.toString()) else dialogRepCountET.visibility = View.GONE

            dialogCancelButton.setOnClickListener { settingsDialog.dismiss() }
            dialogChangeButton.setOnClickListener {
                // todo changing timestamp is not supported as it's a primary key

                var duration:String? = dialogTimeET.text.toString()
                if (duration.isNullOrEmpty())
                    duration = null
                val newLog:ExerciseLog = ExerciseLog(
                    timestamp = exerciseLog.timestamp,
                    exercise = exerciseLog.exercise,
                    duration = duration,
                    distance = dialogDistanceET.text.toString().toFloatOrNull(),
                    weight = dialogWeightET.text.toString().toFloatOrNull(),
                    sets = dialogSetCountET.text.toString().toIntOrNull(),
                    reps = dialogRepCountET.text.toString().toIntOrNull(),
                )

                mainViewModel.updateExerciseLog(newLog)
                settingsDialog.dismiss()

            }
        }



        settingsDialog.setContentView(dialogBinding.root)


        settingsDialog.show()
    }
}