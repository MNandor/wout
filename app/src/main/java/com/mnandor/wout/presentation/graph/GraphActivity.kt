package com.mnandor.wout.presentation.graph

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.Anchor
import com.anychart.enums.HoverMode
import com.anychart.enums.Position
import com.anychart.enums.TooltipPositionMode
import com.mnandor.wout.R
import com.mnandor.wout.WoutApplication
import com.mnandor.wout.data.entities.Completion
import com.mnandor.wout.databinding.ActivityGraphBinding


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


        viewModel.getLast30DaysOfRelevantLogs(exerciseName)

        binding.graphSummarizeSwitch.setOnCheckedChangeListener{_, state:Boolean ->
            viewModel.setSummarizeToggle(state)
        }

        val arrayAdapter = ArrayAdapter.createFromResource(this, R.array.traits, android.R.layout.simple_spinner_item)

        binding.graphTraitSpinner.adapter = arrayAdapter

        binding.graphTraitSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                val selection = arrayAdapter.getItem(position)
                viewModel.setTrait(selection.toString())

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        };

        viewModel.data.observe(this, Observer{items ->
            Log.d("GraphView", "# $items")

            setUpGraph(items)

        })

    }


    var cartesian: Cartesian? = null
    private fun setUpGraph(items: List<Pair<String, Float>>){
        val anyChartView = binding.anyChartView

        if (cartesian == null)
            cartesian = AnyChart.column()

        anyChartView.clear()
        cartesian!!.removeAllSeries()

        val data: MutableList<DataEntry> = ArrayList()

        for (item in items){
            data.add(ValueDataEntry(item.first, item.second))
        }

        Log.d("GraphView", "# $data")


        val column: Column = cartesian!!.column(data)

        column.tooltip()
            .titleFormat("{%X}")
            .position(Position.CENTER_BOTTOM)
            .anchor(Anchor.CENTER_BOTTOM)
            .offsetX(0.0)
            .offsetY(5.0)
            .format("\${%Value}{groupsSeparator: }")

        //cartesian.animation(true)
        cartesian!!.title("Top 10 Cosmetic Products by Revenue")

        cartesian!!.yScale().minimum(0.0)

        cartesian!!.yAxis(0).labels().format("\${%Value}{groupsSeparator: }")

        cartesian!!.tooltip().positionMode(TooltipPositionMode.POINT)
        cartesian!!.interactivity().hoverMode(HoverMode.BY_X)

        cartesian!!.xAxis(0).title("Product")
        cartesian!!.yAxis(0).title("Revenue")

        anyChartView.setChart(cartesian)

    }


}