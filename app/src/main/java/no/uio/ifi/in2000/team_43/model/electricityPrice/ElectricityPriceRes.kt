package no.uio.ifi.in2000.team_43.model.electricityPrice

//cannot fix weak warnings because the API uses this exact phrasing
@Suppress("PropertyName")
data class ElectricityPriceRes(
    val NOK_per_kWh: Double,
    val EUR_per_kWh: Double,
    val EXR: Double,
    val time_start: String,
    val time_end: String
)

