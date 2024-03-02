package com.mnandor.wout.presentation.graph

import android.R
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.Anchor
import com.anychart.enums.HoverMode
import com.anychart.enums.Position
import com.anychart.enums.TooltipPositionMode
import com.mnandor.wout.WoutApplication
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


        setUpGraph()
    }


    private fun setUpGraph(){
        val anyChartView = binding.anyChartView

        val cartesian: Cartesian = AnyChart.column()

        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("Rouge", 80540))
        data.add(ValueDataEntry("Foundation", 94190))
        data.add(ValueDataEntry("Mascara", 102610))
        data.add(ValueDataEntry("Lip gloss", 110430))
        data.add(ValueDataEntry("Lipstick", 128000))
        data.add(ValueDataEntry("Nail polish", 143760))
        data.add(ValueDataEntry("Eyebrow pencil", 170670))
        data.add(ValueDataEntry("Eyeliner", 213210))
        data.add(ValueDataEntry("Eyeshadows", 249980))
        data.add(ValueDataEntry("Eyeshadows", 249980))
        data.add(ValueDataEntry("Eyeshadows", 249980))
        data.add(ValueDataEntry("Eyeshadows", 249980))
        data.add(ValueDataEntry("Nail polish", 143760))
        data.add(ValueDataEntry("Eyebrow pencil", 170670))
        data.add(ValueDataEntry("Eyeliner", 213210))
        data.add(ValueDataEntry("Eyeshadows", 249980))
        data.add(ValueDataEntry("1Eyeshadows", 249980))
        data.add(ValueDataEntry("2Eyeshadows", 249980))
        data.add(ValueDataEntry("3Nail polish", 143760))
        data.add(ValueDataEntry("4Eyebrow pencil", 170670))
        data.add(ValueDataEntry("5Eyeliner", 213210))
        data.add(ValueDataEntry("6Eyeshadows", 249980))
        data.add(ValueDataEntry("7Eyeshadows", 249980))
        data.add(ValueDataEntry("8Eyeshadows", 249980))
        data.add(ValueDataEntry("9Eyeshadows", 249980))

        val column: Column = cartesian.column(data)

        column.tooltip()
            .titleFormat("{%X}")
            .position(Position.CENTER_BOTTOM)
            .anchor(Anchor.CENTER_BOTTOM)
            .offsetX(0.0)
            .offsetY(5.0)
            .format("\${%Value}{groupsSeparator: }")

        cartesian.animation(true)
        cartesian.title("Top 10 Cosmetic Products by Revenue")

        cartesian.yScale().minimum(0.0)

        cartesian.yAxis(0).labels().format("\${%Value}{groupsSeparator: }")

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
        cartesian.interactivity().hoverMode(HoverMode.BY_X)

        cartesian.xAxis(0).title("Product")
        cartesian.yAxis(0).title("Revenue")

        anyChartView.setChart(cartesian)
    }


}