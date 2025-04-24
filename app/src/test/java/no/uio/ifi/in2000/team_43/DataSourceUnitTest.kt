package no.uio.ifi.in2000.team_43

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.gson.gson
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team_43.data.met.MetDataSource
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import no.uio.ifi.in2000.team_43.utils.helperfuctions.met.formatDate
import no.uio.ifi.in2000.team_43.utils.helperfuctions.met.getMostRecentStation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File
import java.time.Instant
import java.time.ZonedDateTime

class DataSourceUnitTest{
    @Test
    fun weatherData_isCorrect() = runTest {
        val mockMetDataSource = mockk<MetDataSource>()

        val coordinates = Coordinates(
            latitude = 10.6,
            longitude = 59.9
        )

        coEvery {
            mockMetDataSource.getWeatherStationData(coordinates, 1)
        } returns WeatherStation(
            id = "SN18700",
            name = "Oslo",
        )

        val station = mockMetDataSource.getWeatherStationData(coordinates, 1)

        assertEquals("SN18700", station?.id)
        assertEquals("Oslo", station?.name)
    }

    //test datasource for temp per day
    @Test
    fun tempData_isCorrect() = runTest {
        //Arrange
        val station = WeatherStation(
            id = "SN18700",
            name = "Oslo",
        )

        val tempElement = "mean(air_temperature P1D)"

        val validYears = ValidYears(
            validTo = Instant.parse("2025-01-01T00:00:00Z"),
            validFrom = Instant.parse("2006-04-03T00:00:00.000Z"),
            elementId = tempElement,
            weatherStation = station
        )

        val jsonFixture = File("src/test/java/no/uio/ifi/in2000/team_43/resources/sample_response_temp.json").readText()

        val mockEngine = MockEngine { request ->
            respond(
                content = jsonFixture,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                gson()
            }
        }

        val dataSource = MetDataSource(client)

        //Act
        val result = dataSource.getWeatherData(station, validYears.elementId, "2024-01-01", "2025-01-01")

        //Assert

        //check datalist size
        assertNotNull(result)
        assertEquals(366, result!!.dataList.size)

        //check first element
        val first = result.dataList[0]
        assertEquals(-4.9, first.value, 0.0001)
        assertEquals("degC", first.unit)
        assertEquals("SN18700", first.weatherStation.id)
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), first.timeMeasured)

        //check arbitrary element
        val weather = result.dataList.find { it.timeMeasured == Instant.parse("2024-06-15T00:00:00Z") }
        assertNotNull(weather)
        assertEquals("mean(air_temperature P1D)", weather!!.elementId)
        assertEquals(14.4, weather.value, 0.0001)

        //check last element
        val last = result.dataList[365]
        assertEquals(-2.4, last.value, 0.0001)
        assertEquals("degC", last.unit)
        assertEquals("SN18700", last.weatherStation.id)
        assertEquals(Instant.parse("2024-12-31T00:00:00Z"), last.timeMeasured)
    }

    // test some helper functions used in weatherdata api/datasource
    /*
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
    */
    @Test
    fun formatDate_isCorrect() = runTest {
        val testDate1 = Instant.parse("2023-05-15T12:30:00Z")
        assertEquals("2023-05-15", formatDate(testDate1))

        val testDate2 = Instant.parse("2024-12-31T23:59:59Z")
        assertEquals("2024-12-31", formatDate(testDate2))

        val testDate3 = Instant.parse("2025-01-01T00:00:00Z")
        assertEquals("2025-01-01", formatDate(testDate3))

        val testDate4 = Instant.parse("2023-10-10T12:00:00+01:00")
        assertEquals("2023-10-10", formatDate(testDate4))
    }
}