package com.mnandor.wout.presentation.main

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.os.PersistableBundle
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
import com.mnandor.wout.presentation.EditCompletionDialog
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
            if (items == templates)
                return@Observer

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

        // edge case: if user is editing multiple logs way in the past
        // we might not want to always scroll down
        // rare occurence, keep it like this for now
        viewModel.allLogs.observe(this, Observer { items ->
            items?.let{adapter.setItems(items)}
            adapter.notifyDataSetChanged()
            // adapter.itemCount factors in the separators that display the date
            // items.size does not
            recyclerView.scrollToPosition(adapter.itemCount-1)
        })

        viewModel.allDayTemplates.observe(this, Observer { items ->
           binding.todayIsSelector.setItems(items)

            viewModel.loadLocationSetting()
        })

        viewModel.locationSetting.observe(this, Observer {
            binding.todayIsSelector.setSelection(it)
        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selection", binding.exerciseDropdown.selectedItemPosition)
        outState.putString("timeET", binding.timeET.text.toString())
        outState.putString("distanceET", binding.distanceET.text.toString())
        outState.putString("massET", binding.weightET.text.toString())
        outState.putString("setsET", binding.setCountET.text.toString())
        outState.putString("repsET", binding.repCountET.text.toString())
    }

    private var dontClear = 0
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        dontClear = 1
        savedInstanceState.getString("timeET")?.let {binding.timeET.setText(it)}
        savedInstanceState.getString("distanceET")?.let {binding.distanceET.setText(it)}
        savedInstanceState.getString("massET")?.let {binding.weightET.setText(it)}
        savedInstanceState.getString("setsET")?.let {binding.setCountET.setText(it)}
        savedInstanceState.getString("repsET")?.let {binding.repCountET.setText(it)}

        savedInstanceState.getInt("selection")?.let {
            binding.exerciseDropdown.setSelection(it)
            if (it != 0)
                dontClear = 2
        }
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

        if (dontClear == 0){
            binding.timeET.text.clear()
            binding.distanceET.text.clear()
            binding.weightET.text.clear()
            binding.setCountET.text.clear()
            binding.repCountET.text.clear()

        } else
            dontClear -= 1


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

        val dialog = EditCompletionDialog(this)

        dialog.setCompletion(completion)

        dialog.setUpdateCallback { viewModel.updateExerciseLog(it) }
        dialog.setRedateCallback { oldDate, newDate -> viewModel.changeCompletionDate(oldDate, newDate)  }

        dialog.show()
    }
}