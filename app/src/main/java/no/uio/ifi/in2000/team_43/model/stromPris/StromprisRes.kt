package no.uio.ifi.in2000.team_43.model.stromPris


import java.time.ZonedDateTime

data class StromprisRes(
    val NOK_per_kWh: Double,
    val EUR_per_kWh: Double,
    val EXR: Double,
    val time_start: String,
    val time_end: String
)


fun StromprisRes.toFormattedString(): String {
    return """
        Strømpris:
        NOK/kWh: ${"%.3f".format(NOK_per_kWh)}
        Periode: ${time_start} - ${time_end}
    """.trimIndent()
}