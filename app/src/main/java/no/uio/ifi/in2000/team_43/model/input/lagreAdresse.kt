package no.uio.ifi.in2000.team_43.model.input

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LagreAdresse(
    val latitude: Double,
    val longitude: Double,
    val height: Double,
    val width: Double,
    val angle: Double,
    val direction: Double,
    val addressString: String,
    val lagretNavn: String,
    val numPanel: Int,
    val totalPanelAreal: Double,
    val kwp: Int,
    val pris: Int,
    val MoneySaved: Float,
    val uniqueId: String = UUID.randomUUID().toString()  // Genererer UUID hvis det ikke blir spesifisert
)
