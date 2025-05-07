package no.uio.ifi.in2000.team_43.ui.info.grafStuff

import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import no.uio.ifi.in2000.team_43.model.garfStuff.GrafMeasurement

@Composable
fun MonthlyChartWithValueDisplay(
    selectedMonth: Int, // 1-basert måned, f.eks. 1 for januar, 12 for desember
    allMeasurements: List<List<GrafMeasurement>>
) {
    var selectedValue by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        LineChartForMonths(
            selectedMonth = selectedMonth,  // Valgt måned (1-12)
            allMeasurements = allMeasurements,
            onValueSelected = { x, y ->
                selectedDate = "Dato: ${x.toInt()}"
                selectedValue = "$y kwh"
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Vis dato og verdi i to bokser ved siden av hverandre
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.cardElevation(4.dp), // Bruk riktig metode for elevation
                shape = MaterialTheme.shapes.medium
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedDate ?: "Trykk på grafen for å vise dato",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.cardElevation(4.dp), // Bruk riktig metode for elevation
                shape = MaterialTheme.shapes.medium
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedValue ?: "Trykk på grafen for å vise verdi",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}




@Composable
fun LineChartForMonths(
    selectedMonth: Int,
    allMeasurements: List<List<GrafMeasurement>>,
    onValueSelected: (Float, Float) -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val txtColor = if (isDarkMode) Color.WHITE else Color.BLACK

    val chartRef = remember { mutableStateOf<LineChart?>(null) }

    // Hent data for valgt måned (1–12), juster for 0-indeksert liste
    val measurements = allMeasurements.getOrNull(selectedMonth - 1) ?: emptyList()

    AndroidView(
        factory = { ctx ->
            LineChart(ctx).also { chartRef.value = it }
        },
        update = { chart ->
            chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let { onValueSelected(it.x, it.y) }
                }

                override fun onNothingSelected() {
                    onValueSelected(-1f, -1f)
                }
            })

            val entries = measurements.map { Entry(it.time, it.value) }

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
                setLabelCount(10, false)
                axisMinimum = 0.5f
                axisMaximum = 31.5f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString() // Viser dag (1–31)
                    }
                }
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
