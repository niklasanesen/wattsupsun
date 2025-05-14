package no.uio.ifi.in2000.team_43.ui.info.graphComponents

import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import no.uio.ifi.in2000.team_43.model.graphRes.GraphMeasurement
import no.uio.ifi.in2000.team_43.model.graphRes.MonthValueFormatter
import android.util.Log
import java.text.NumberFormat
import java.util.Locale
import androidx.core.graphics.toColorInt

@Composable
fun YearlyChartWithValueDisplay(
    allMeasurements: List<GraphMeasurement>,
    modifier: Modifier = Modifier,
    onValueSelected: (String, String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        LineChartWithSwitch(
            allMeasurements = allMeasurements,
            onValueSelected = { x, y ->
                onValueSelected(x.toInt().toString(), NumberFormat.getInstance(Locale("no", "NO")).format(y.toInt()))
            }
        )
    }
}


@Composable
fun LineChartWithSwitch(
    allMeasurements: List<GraphMeasurement>,
    onValueSelected: (Float, Float) -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val txtColor = if (isDarkMode) Color.WHITE else Color.BLACK

    val chartRef = remember { mutableStateOf<LineChart?>(null) }

    AndroidView(
        factory = { ctx ->
            LineChart(ctx).also { chartRef.value = it }
        },
        update = { chart ->
            chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let {
                        onValueSelected(it.x, it.y)
                        Log.d("YearlyChart", "onValueSelected called: x=${it.x}, y=${it.y}") // Log for debugging
                    }
                }

                override fun onNothingSelected() {
                    onValueSelected(-1f, -1f)
                    Log.d("YearlyChart", "No value selected")  // Log for debugging
                }
            })

            val entries = allMeasurements.map { Entry(it.time, it.value) }

            val dataSet = LineDataSet(entries, "Produksjon").apply {
                mode = LineDataSet.Mode.CUBIC_BEZIER
                color = "#304745".toColorInt() // dark green
                valueTextColor = txtColor
                lineWidth = 4f
                circleRadius = 0f
                setCircleColor("#304745".toColorInt())
                setDrawValues(false)
                setDrawCircles(true)
                setDrawHighlightIndicators(true)
                highLightColor = Color.RED
            }

            chart.data = LineData(dataSet)

            chart.axisLeft.isEnabled = false
            chart.axisRight.apply {
                isEnabled = true
                textColor = txtColor
                setDrawGridLines(false)
            }

            chart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = txtColor
                granularity = 1f
                valueFormatter = MonthValueFormatter()
                setLabelCount(allMeasurements.size, true)
                axisMinimum = allMeasurements.minOfOrNull { it.time }?.minus(0.5f) ?: 0f
                axisMaximum = allMeasurements.maxOfOrNull { it.time }?.plus(0.5f) ?: 12f
            }

            chart.legend.isEnabled = false
            chart.description.isEnabled = false

            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}