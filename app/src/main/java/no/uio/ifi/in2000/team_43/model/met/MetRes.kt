package no.uio.ifi.in2000.team_43.model.met


import androidx.compose.ui.layout.Measured
import com.mapbox.maps.extension.style.expressions.dsl.generated.min
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
    val validFrom: Instant?,
    val validTo: Instant?,
    val elementId: String,
    val weatherStation: WeatherStation
)

@Serializable
data class AllValidYears(
    val validYearsList: List<ValidYears>
)

@Serializable
data class WeatherData(
    val elementId: String,
    val value: Double,
    val unit: String,
    val weatherStation: WeatherStation,
    val timeMeasured: Instant?
)

@Serializable
data class WeatherList(
    val dataList: List<WeatherData>
)

@Serializable
data class AggregatedData(
    val dailyObservation: List<Pair<Instant, WeatherList>>
)

@Serializable
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

