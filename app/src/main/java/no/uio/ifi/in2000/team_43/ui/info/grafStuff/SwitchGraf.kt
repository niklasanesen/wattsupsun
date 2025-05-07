package no.uio.ifi.in2000.team_43.ui.info.grafStuff

import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import no.uio.ifi.in2000.team_43.model.garfStuff.GrafMeasurement
import no.uio.ifi.in2000.team_43.model.garfStuff.MonthValueFormatter
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border

@Composable
fun YearlyChartWithValueDisplay(
    allMeasurements: List<GrafMeasurement>,
    isLoading: Boolean, // Flag for å vise lastesirkel
    modifier: Modifier = Modifier // Legger til Modifier som parameter
) {
    var selectedValue by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier // Bruker den modifieren som sendes inn
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        LineChartWithSwitch(
            allMeasurements = allMeasurements,
            onValueSelected = { x, y ->
                selectedValue = "Måned: ${x.toInt()}, Verdi: $y kWh"
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Vise lastesirkel hvis isLoading er true
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Tekst i boks hvis data er tilgjengelig
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp) // Legger til litt padding rundt boksen
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) // Bakgrunnsfarge for boksen
                    .border(2.dp, MaterialTheme.colorScheme.primary) // Legger til kantlinje
                    .padding(16.dp) // Padding inni boksen
            ) {
                Text(
                    text = selectedValue ?: "Trykk på grafen for å vise verdi",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


@Composable
fun LineChartWithSwitch(
    allMeasurements: List<GrafMeasurement>,
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
