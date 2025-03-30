package no.uio.ifi.in2000.team_43.model.met

import kotlinx.serialization.Serializable

data class MetRes(
    val context: String,
    val type: String,
    val apiVersion: String,
    val license: String,
    val createdAt: String,
    val queryTime: Double,
    val currentItemCount: Int,
    val itemsPerPage: Int,
    val offset: Int,
    val totalItemCount: Int,
    val currentLink: String,
    val data: List<WeatherData>
)

data class WeatherData(
    val sourceId: String,
    val referenceTime: String,
    val observations: List<Observation>
)

data class Observation(
    val elementId: String,
    val value: Double,
    val unit: String,
    val level: Level? = null,
    val timeOffset: String,
    val timeResolution: String,
    val timeSeriesId: Int,
    val performanceCategory: String,
    val exposureCategory: String,
    val qualityCode: Int
)

data class Level(
    val levelType: String,
    val unit: String,
    val value: Int
)

@Serializable
data class WeatherStation(
    val name: String,
    val id: String
)