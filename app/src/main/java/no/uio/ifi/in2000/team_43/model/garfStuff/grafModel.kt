package no.uio.ifi.in2000.team_43.model.garfStuff


import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter



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