package no.uio.ifi.in2000.team_43

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team_43.data.met.MetDataSource
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import no.uio.ifi.in2000.team_43.utils.helperfuctions.met.getMostRecentStation
import org.junit.Test
import org.junit.Assert.*

class WeatherDataTest {
    @Test
    fun weatherData_isCorrect() = runTest {
        val mockMetDataSource = mockk<MetDataSource>()

        val coordinates = Coordinates(
            latitude = 10.6,
            longitude = 59.9
        )

        coEvery {
            mockMetDataSource.getWeatherStationData(coordinates)
        } returns WeatherStation(
            id = "SN18700",
            name = "Oslo",
        )

        val station = mockMetDataSource.getWeatherStationData(coordinates)

        assertEquals("SN18700", station?.id)
        assertEquals("Oslo", station?.name)
    }

    // test some helper functions used in weatherdata api/datasource
    @Test
    fun getMostRecentStation_isCorrect() = runTest {
        val jsonParser = com.google.gson.JsonParser()

        val checkStationJsonStr = """
            [{"sourceId":"SN70850:0","level":{"levelType":"height_above_ground","unit":"m","value":2},"validFrom":"1939-12-01T00:00:00.000Z","validTo":"2012-06-16T00:00:00.000Z","timeOffset":"PT0H","timeResolution":"P1Y","timeSeriesId":0,"elementId":"mean(air_temperature P1Y)","unit":"degC","performanceCategory":"C","exposureCategory":"2","status":"Authoritative","uri":"https://frost.met.no/observations/v0.jsonld?sources=SN70850:0&referencetime=1939-12-01T00:00:00.000Z/2012-06-16T00:00:00.000Z&elements=mean(air_temperature P1Y)&timeoffsets=PT0H&timeresolutions=P1Y&timeseriesids=0&performancecategories=C&exposurecategories=2&levels=2.0"},{"sourceId":"SN70850:0","level":{"levelType":"height_above_ground","unit":"m","value":2},"validFrom":"2012-06-16T00:00:00.000Z","validTo":"2012-11-19T00:00:00.000Z","timeOffset":"PT0H","timeResolution":"P1Y","timeSeriesId":0,"elementId":"mean(air_temperature P1Y)","unit":"degC","performanceCategory":"C","exposureCategory":"2","status":"Authoritative","uri":"https://frost.met.no/observations/v0.jsonld?sources=SN70850:0&referencetime=2012-06-16T00:00:00.000Z/2012-11-19T00:00:00.000Z&elements=mean(air_temperature P1Y)&timeoffsets=PT0H&timeresolutions=P1Y&timeseriesids=0&performancecategories=C&exposurecategories=2&levels=2.0"},{"sourceId":"SN70850:0","level":{"levelType":"height_above_ground","unit":"m","value":2},"validFrom":"2012-11-19T00:00:00.000Z","timeOffset":"PT0H","timeResolution":"P1Y","timeSeriesId":0,"elementId":"mean(air_temperature P1Y)","unit":"degC","performanceCategory":"C","exposureCategory":"2","status":"Authoritative","uri":"https://frost.met.no/observations/v0.jsonld?sources=SN70850:0&referencetime=2012-11-19T00:00:00.000Z/9999-12-31T23:59:59Z&elements=mean(air_temperature P1Y)&timeoffsets=PT0H&timeresolutions=P1Y&timeseriesids=0&performancecategories=C&exposurecategories=2&levels=2.0"}]
        """.trimIndent()
        val checkStationJsonObj = jsonParser.parse(checkStationJsonStr).asJsonArray

        val correctStationJsonStr = """
            {"sourceId":"SN70850:0","level":{"levelType":"height_above_ground","unit":"m","value":2},"validFrom":"2012-11-19T00:00:00.000Z","timeOffset":"PT0H","timeResolution":"P1Y","timeSeriesId":0,"elementId":"mean(air_temperature P1Y)","unit":"degC","performanceCategory":"C","exposureCategory":"2","status":"Authoritative","uri":"https://frost.met.no/observations/v0.jsonld?sources=SN70850:0&referencetime=2012-11-19T00:00:00.000Z/9999-12-31T23:59:59Z&elements=mean(air_temperature P1Y)&timeoffsets=PT0H&timeresolutions=P1Y&timeseriesids=0&performancecategories=C&exposurecategories=2&levels=2.0"}
        """.trimIndent()
        val correctStationJsonObj = jsonParser.parse(correctStationJsonStr).asJsonObject

        val res = getMostRecentStation(checkStationJsonObj)
        assertEquals(res, correctStationJsonObj)
    }
}