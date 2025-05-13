package no.uio.ifi.in2000.team_43.model.input

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SavedAddress(
    val latitude: Double,
    val longitude: Double,
    val height: Double,
    val width: Double,
    val angle: Double,
    val direction: Double,
    val addressString: String,
    val savedName: String,
    val numPanel: Int,
    val totalPanelAreal: Double,
    val kwp: Int,
    val price: Int,
    val moneySaved: Float,
    val uniqueId: String = UUID.randomUUID().toString()  // Generates UUID if it is not specified
)
