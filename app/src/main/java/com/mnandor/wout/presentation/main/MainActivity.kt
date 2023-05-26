package com.mnandor.wout.presentation.main

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mnandor.wout.R
import com.mnandor.wout.WoutApplication
import com.mnandor.wout.data.entities.Completion
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.databinding.ActivityMainBinding
import com.mnandor.wout.databinding.DialogEditLogBinding
import com.mnandor.wout.presentation.exercises.ExercisesActivity
import com.mnandor.wout.presentation.schedule.ScheduleActivity
import com.mnandor.wout.presentation.locations.LocationsActivity
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var templates: List<Exercise>? = null

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as WoutApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setClickListeners()


        val adapter = MainRecyclerAdapter()

        viewModel.allVisibleTemplates.observe(this, Observer { items ->
            val aadapter = ArrayAdapter<String>(
                this,
                R.layout.spinner_item, items.map { it->it.name }
            )

            binding.exerciseDropdown.adapter = aadapter

            aadapter.notifyDataSetChanged()

            templates = items

            adapter.setRelevantExercises(items.map { it.name })
            adapter.notifyDataSetChanged()


        })

        viewModel.trendlinePrediction.observe(this, Observer{
            binding.repCountET.setHint("/"+it.toString())
        })


        viewModel.openCount.observe(this, Observer {
//            Toast.makeText(this, "You have opened the app "+it+" times.", Toast.LENGTH_SHORT).show()
        })

        viewModel.storeAppOpened()


        val recyclerView = binding.exerciseLogsRecycle

        adapter.setDeleteCallback { deleteExerciseLog(it) }
        adapter.setEditCallback { editExerciseLog(it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.allLogs.observe(this, Observer { items ->
            items?.let{adapter.setItems(items)}
            adapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(items.size)
        })

        viewModel.allDayTemplates.observe(this, Observer { items ->
           binding.todayIsSelector.setItems(items)

            viewModel.loadLocationSetting()
        })

        viewModel.locationSetting.observe(this, Observer {
            binding.todayIsSelector.setSelection(it)
        })

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadLocationSetting()
    }

    private fun setClickListeners() {
        binding.addButton.setOnClickListener {
            addExerciseLog()
        }

        binding.addButton.setOnLongClickListener {
            openSettingsPopup(it)

            return@setOnLongClickListener true // yes, consume event
        }

        binding.exerciseDropdown.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (templates?.size != 0)
                        updateUIAfterDropdown(templates?.get(binding.exerciseDropdown.selectedItemPosition))
                }
            }

        binding.todayIsSelector.setCallback {
            viewModel.setFilter(it)
        }
    }

    private fun openSettingsPopup(popupButton: View) {
        val popupMenu = PopupMenu(this, popupButton)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
            when (item?.itemId) {
                R.id.exercises -> {
                    openConfigActivity()
                    // Navigate to Exercises activity
                    true
                }
                R.id.locations -> {
                    // Navigate to Locations activity
                    openTemplatesActivity()
                    true
                }
                R.id.schedule -> {
                    openScheduleActivity()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }


    private var selectedItem: Exercise? = null

    private fun updateUIAfterDropdown(item: Exercise?){
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
            viewModel.calculateTrendline(item)
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

        viewModel.insert(
            Completion(
            currentTime,
            binding.exerciseDropdown.selectedItem.toString(),
            if(selectedItem!!.usesTime and binding.timeET.text.toString().isNotEmpty()) binding.timeET.text.toString() else null,
            if(selectedItem!!.usesDistance) binding.distanceET.text.toString().toFloatOrNull() else null,
            if(selectedItem!!.usesWeight) binding.weightET.text.toString().toFloatOrNull() else null,
            if(selectedItem!!.usesSetCount) binding.setCountET.text.toString().toIntOrNull() else null,
            if(selectedItem!!.usesRepCount) binding.repCountET.text.toString().toIntOrNull() else null,

        )
        )

    }

    private fun openConfigActivity(){
        val intent = Intent(this, ExercisesActivity::class.java)
        startActivity(intent)
    }

    private fun openTemplatesActivity(){
        val intent = Intent(this, LocationsActivity::class.java)
        startActivity(intent)
    }

    private fun openScheduleActivity(){
        val intent = Intent(this, ScheduleActivity::class.java)
        startActivity(intent)
    }

    public fun deleteExerciseLog(completion: Completion){
        AlertDialog.Builder(this)
            .setTitle(completion.exercise)
            .setMessage("Do you really want to delete this exercise log?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, whichButton ->
                    viewModel.deleteExerciseLog(completion)
                })
            .setNegativeButton(android.R.string.no, null).show()
    }

    public fun editExerciseLog(completion: Completion){
        Toast.makeText(this, completion.exercise, Toast.LENGTH_SHORT).show()
        val settingsDialog = Dialog(this)

        val dialogBinding = DialogEditLogBinding.inflate(layoutInflater)

        with(dialogBinding){
            dialogLogName.text = completion.exercise
            dialogLogDateET.setText(completion.timestamp.split(" ")[0])
            dialogLogTimeET.setText(completion.timestamp.split(" ")[1])

            if (completion.duration != null) dialogTimeET.setText(completion.duration) else dialogTimeET.visibility = View.GONE
            if (completion.distance != null) dialogDistanceET.setText(completion.distance.toString()) else dialogDistanceET.visibility = View.GONE
            if (completion.weight != null) dialogWeightET.setText(completion.weight.toString()) else dialogWeightET.visibility = View.GONE
            if (completion.sets != null) dialogSetCountET.setText(completion.sets.toString()) else dialogSetCountET.visibility = View.GONE
            if (completion.reps != null) dialogRepCountET.setText(completion.reps.toString()) else dialogRepCountET.visibility = View.GONE

            dialogCancelButton.setOnClickListener { settingsDialog.dismiss() }
            dialogChangeButton.setOnClickListener {
                // todo changing timestamp is not supported as it's a primary key

                var duration:String? = dialogTimeET.text.toString()
                if (duration.isNullOrEmpty())
                    duration = null
                val newLog: Completion = Completion(
                    timestamp = completion.timestamp,
                    exercise = completion.exercise,
                    duration = duration,
                    distance = dialogDistanceET.text.toString().toFloatOrNull(),
                    weight = dialogWeightET.text.toString().toFloatOrNull(),
                    sets = dialogSetCountET.text.toString().toIntOrNull(),
                    reps = dialogRepCountET.text.toString().toIntOrNull(),
                )

                viewModel.updateExerciseLog(newLog)
                settingsDialog.dismiss()

            }
        }



        settingsDialog.setContentView(dialogBinding.root)


        settingsDialog.show()
    }
}