package no.uio.ifi.in2000.team_43.model.panel

data class SunPanel(
    val navn: String,
    val pris: Int,         // Pris i NOK
    val effekt: Int,       // Effekt i Watt (W)
    val bildeResId: Int,   // Referanse til bildet i res/drawable
    val len: Double,
    val width: Double

)
