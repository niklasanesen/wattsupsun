package no.uio.ifi.in2000.team_43.model.input

import kotlinx.serialization.Serializable

@Serializable
data class LagreAdresse(
    val longitude: Double,     // Lengdegrad
    val latitude: Double,      // Breddegrad
    val height: Double,        // Høyde
    val width: Double,         // Bredde
    val angle: Double,         // Vinkel
    val direction: Double,     // Retning
    val addressString: String, // Full adresse som tekst
    val lagretNavn: String     // Brukerens navn på adressen (f.eks. "Hjem")
)
