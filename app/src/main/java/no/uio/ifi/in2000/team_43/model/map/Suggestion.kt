package no.uio.ifi.in2000.team_43.model.map


data class Suggestion(
    val name: String,
    val formattedAddress: String,
    val streetAddress: String,
    val postalCode: String,
    val city: String,
    val country: String
)