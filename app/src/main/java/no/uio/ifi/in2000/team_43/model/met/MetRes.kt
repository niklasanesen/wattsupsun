package no.uio.ifi.in2000.team_43.model.met


import com.mapbox.maps.extension.style.expressions.dsl.generated.min
import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.team_43.utils.serializers.InstantSerializer
import java.time.Instant
import kotlin.math.min



fun calcCCF(x: Int): Double {
    return 0.1
}

fun calcSCF(x: Int): Double {
    return 0.1
}

fun calcGHI(x: Int, y: Int, z: Int): Double {
    return 0.1
}


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

@Serializable
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)