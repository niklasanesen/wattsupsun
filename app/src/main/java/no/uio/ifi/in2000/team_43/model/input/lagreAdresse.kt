package no.uio.ifi.in2000.team_43.model.input


import kotlinx.serialization.Serializable

@Serializable
data class LagreAdresse(
    val longitude: Double, // Lengdegrad (Double)
    val latitude: Double,  // Breddegrad (Double)
    val height: Double,    // Høyde (Double)
    val width: Double,     // Bredde (Double)
    val angle: Double,     // Vinkel (Double)
    val direction: Double  // Retning (Double)
)
