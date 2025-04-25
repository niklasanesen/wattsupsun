package no.uio.ifi.in2000.team_43.ui.info

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
data class Measurement(val time: Float, val value: Float)

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
fun ChartWithValueDisplay() {
    var selectedValue by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        LineChartComposable(onValueSelected = { x, y ->
            selectedValue = "Måned: ${MonthValueFormatter().getFormattedValue(x)}, Verdi: $y"
        })

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = selectedValue ?: "Trykk på grafen for å vise verdi",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun LineChartComposable(onValueSelected: (Float, Float) -> Unit) {
    val isDarkMode = isSystemInDarkTheme()
    val txtColor = if (isDarkMode) Color.WHITE else Color.BLACK

    val measurements = listOf(
        Measurement(0f, 2f), Measurement(1f, 2.3f), Measurement(2f, 1.9f),
        Measurement(3f, 3.2f), Measurement(4f, 4.1f), Measurement(5f, 4.3f),
        Measurement(6f, 4.0f), Measurement(7f, 3.7f), Measurement(8f, 3.1f),
        Measurement(9f, 2.4f), Measurement(10f, 2.1f), Measurement(11f, 2.0f)
    )

    AndroidView(
        factory = { ctx ->
            LineChart(ctx).apply {
                val entries = measurements.map { Entry(it.time, it.value) }

                val dataSet = LineDataSet(entries, "Temperatur").apply {
                    mode = LineDataSet.Mode.CUBIC_BEZIER // 👈 Glatt kurve
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
                    setLabelCount(12, true) // 👈 Vis nøyaktig 12 labels (tving)
                    axisMinimum = -0.5f
                    axisMaximum = 11.5f
                    valueFormatter = MonthValueFormatter()
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
                        if (e != null) {
                            onValueSelected(e.x, e.y)
                        }
                    }

                    override fun onNothingSelected() {
                        onValueSelected(-1f, -1f)
                    }
                })

                animateX(1000) // 👈 Animasjon inn

                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

