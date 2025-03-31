package no.uio.ifi.in2000.team_43.model.met


import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.team_43.utils.serializers.InstantSerializer
import java.time.Instant

@Serializable
data class WeatherStation(
    val name: String,
    val id: String
)

@Serializable
data class ValidYears(
    @Serializable(with = InstantSerializer::class)
    val validTo: Instant?,
    val elementId: String,
    val weatherStation: WeatherStation
)

@Serializable
data class WeatherData(
    val elementId: String,
    val value: Double,
    val unit: String,
    val weatherStation: WeatherStation
)

@Serializable
data class WeatherList(
    val dataList: List<WeatherData>
)

