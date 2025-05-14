package no.uio.ifi.in2000.team_43.model.graphRes


import com.github.mikephil.charting.formatter.ValueFormatter


// Data-class for the values
data class GraphMeasurement(val time: Float, val value: Float)

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
        else -> "Ugyldig måned" // If month is outside of the interval 1-12
    }
}


// Formats for the X-axis (shows the months)
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