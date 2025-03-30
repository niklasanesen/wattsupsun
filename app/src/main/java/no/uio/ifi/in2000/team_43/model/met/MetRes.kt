package no.uio.ifi.in2000.team_43.model.met

import kotlinx.serialization.Serializable
import java.time.Instant

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
/*
data class WeatherData(
    val sourceId: String,
    val referenceTime: String,
    val observations: List<Observation>
)
*/
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

@Serializable
data class ValidYears(
    val validTo: Instant,
    val elementId: String,
    val weatherStation: WeatherStation
)

@Serializable
data class WeatherData(
    //TODO check which values are necessary from this endpoint: /observations/v0.{format}
    /*
    A typical response will look like this:
    request url: https://frost.met.no/observations/v0.jsonld?sources=SN27470&referencetime=2007-07-31%2F2008-07-31&elements=cloud_area_fraction
    response:
    {
  "@context": "https://frost.met.no/schema",
  "@type": "ObservationResponse",
  "apiVersion": "v0",
  "license": "https://creativecommons.org/licenses/by/3.0/no/",
  "createdAt": "2025-03-30T16:40:34Z",
  "queryTime": 1.067,
  "currentItemCount": 2,
  "itemsPerPage": 2,
  "offset": 0,
  "totalItemCount": 2,
  "currentLink": "https://frost.met.no/observations/v0.jsonld?sources=SN27470&referencetime=2007-07-31%2F2008-07-31&elements=cloud_area_fraction",
  "data": [
    {
      "sourceId": "SN27470:0",
      "referenceTime": "2008-03-04T18:00:00.000Z",
      "observations": [
        {
          "elementId": "cloud_area_fraction",
          "value": 0,
          "unit": "octas",
          "timeOffset": "PT0H",
          "timeResolution": "PT6H",
          "timeSeriesId": 0,
          "performanceCategory": "C",
          "exposureCategory": "2",
          "qualityCode": 1
        }
      ]
    },
    {
      "sourceId": "SN27470:0",
      "referenceTime": "2008-07-23T06:00:00.000Z",
      "observations": [
        {
          "elementId": "cloud_area_fraction",
          "value": 6,
          "unit": "octas",
          "timeOffset": "PT0H",
          "timeResolution": "PT6H",
          "timeSeriesId": 0,
          "performanceCategory": "C",
          "exposureCategory": "2",
          "qualityCode": 1
        }
      ]
    }
  ]
}

     */
)