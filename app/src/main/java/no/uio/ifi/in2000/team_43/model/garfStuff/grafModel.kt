package no.uio.ifi.in2000.team_43.model.garfStuff


import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter



// Data-klasse for verdiene
data class GrafMeasurement(val time: Float, val value: Float)

fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Januar"
        2 -> "Februar"
        3 -> "Mars"
        4 -> "April"
        5 -> "Mai"
        6 -> "Juni"
        7 -> "Juli"
        8 -> "August"
        9 -> "September"
        10 -> "Oktober"
        11 -> "November"
        12 -> "Desember"
        else -> "Ugyldig måned" // Hvis måneden er utenfor intervallet 1-12
    }
}


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