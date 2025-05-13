package no.uio.ifi.in2000.team_43.model.electricityPrice


data class ElectricityPriceRes(
    val nokPerKWh: Double,
    val eurPerKWh: Double,
    val exr: Double,
    val timeStart: String,
    val timeEnd: String
)


fun ElectricityPriceRes.toFormattedString(): String {
    return """
        ElectricityPrice:
        NOK/kWh: ${"%.3f".format(nokPerKWh)}
        Period: $timeStart - $timeEnd
    """.trimIndent()
}