package no.uio.ifi.in2000.team_43.ui.sharedScreenComponents

import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


// Data-klasse for verdiene
data class GrafMeasurement(val time: Float, val value: Float)

// Formatter for X-aksen (viser månedene)
class MonthValueFormatter : ValueFormatter() {
    private val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
        "Jul", "Aug", "Sep", "Okt", "Nov", "Des"
    )

    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index in months.indices) months[index] else value.toString()
    }
}


@Composable
fun ChartWithValueDisplay(
    selectedMonth: Int?, // Nullable
    allMeasurements: List<GrafMeasurement>
) {
    var selectedValue by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        if (selectedMonth != null && selectedMonth in 1..12) {
            LineChartForMonths(
                selectedMonth = selectedMonth,
                allMeasurements = allMeasurements,
                onValueSelected = { x, y ->
                    selectedValue = "Dag: ${x.toInt()}, Verdi: $y kWh"
                }
            )
        } else {
            LineChartWithSwitch(
                allMeasurements = allMeasurements,
                onValueSelected = { x, y ->
                    selectedValue = "Måned: ${x.toInt()}, Verdi: $y kWh"
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = selectedValue ?: "Trykk på grafen for å vise verdi",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun LineChartWithSwitch(
    allMeasurements: List<GrafMeasurement>,
    onValueSelected: (Float, Float) -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val txtColor = if (isDarkMode) Color.WHITE else Color.BLACK

    AndroidView(
        factory = { ctx ->
            LineChart(ctx).apply {
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        e?.let { onValueSelected(it.x, it.y) }
                    }

                    override fun onNothingSelected() {
                        onValueSelected(-1f, -1f)
                    }
                })

                axisLeft.isEnabled = false
                axisRight.apply {
                    isEnabled = true
                    textColor = txtColor
                    setDrawGridLines(false)
                }

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.textColor = txtColor
                xAxis.granularity = 1f
                xAxis.valueFormatter = MonthValueFormatter()

                legend.isEnabled = false
                description.isEnabled = false
            }
        },
        update = { chart ->
            val entries = allMeasurements.map { Entry(it.time, it.value) }

            val dataSet = LineDataSet(entries, "Produksjon").apply {
                mode = LineDataSet.Mode.CUBIC_BEZIER
                color = Color.BLUE
                valueTextColor = txtColor
                lineWidth = 2.5f
                circleRadius = 5f
                setCircleColor(Color.BLUE)
                setDrawValues(false)
                setDrawCircles(true)
                setDrawHighlightIndicators(true)
                highLightColor = Color.RED
            }

            chart.data = LineData(dataSet)

            chart.xAxis.setLabelCount(allMeasurements.size, true)
            chart.xAxis.axisMinimum = allMeasurements.minOfOrNull { it.time }?.minus(0.5f) ?: 0f
            chart.xAxis.axisMaximum = allMeasurements.maxOfOrNull { it.time }?.plus(0.5f) ?: 12f

            chart.animateX(1000)
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}


@Composable
fun LineChartForMonths(
    selectedMonth: Int,
    allMeasurements: List<GrafMeasurement>,
    onValueSelected: (Float, Float) -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val txtColor = if (isDarkMode) Color.WHITE else Color.BLACK

    val measurements = allMeasurements

    key(selectedMonth) { // 👈 tving re-komposisjon når måned endres
        AndroidView(
            factory = { ctx ->
                LineChart(ctx).apply {
                    val entries = measurements.mapIndexed { index, m -> Entry(index.toFloat(), m.value) }

                    val dataSet = LineDataSet(entries, "Produksjon").apply {
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        color = Color.BLUE
                        valueTextColor = txtColor
                        lineWidth = 2.5f
                        circleRadius = 5f
                        setCircleColor(Color.BLUE)
                        setDrawValues(false)
                        setDrawCircles(true)
                        setDrawHighlightIndicators(true)
                        highLightColor = Color.RED
                    }

                    data = LineData(dataSet)

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        textColor = txtColor
                        granularity = 1f
                        setLabelCount(measurements.size, true)
                        axisMinimum = -0.5f
                        axisMaximum = measurements.size.toFloat() + 0.5f
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return value.toInt().toString()
                            }
                        }
                    }

                    axisLeft.isEnabled = false
                    axisRight.apply {
                        isEnabled = true
                        textColor = txtColor
                        setDrawGridLines(false)
                    }

                    legend.isEnabled = false
                    description.isEnabled = false

                    setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                        override fun onValueSelected(e: Entry?, h: Highlight?) {
                            e?.let { onValueSelected(it.x, it.y) }
                        }

                        override fun onNothingSelected() {
                            onValueSelected(-1f, -1f)
                        }
                    })

                    animateX(1000)
                    invalidate()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}