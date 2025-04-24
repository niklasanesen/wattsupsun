package no.uio.ifi.in2000.team_43


import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team_43.data.met.MetDataSource
import no.uio.ifi.in2000.team_43.data.met.MetRepository
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherData
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class MetRepositoryTest{
    private lateinit var repository: MetRepository
    private val mockDataSource = mockk<MetDataSource>()

    @Before
    fun setup() {
        // Injecting mock into repository using constructor injection
        repository = MetRepository(mockDataSource)
    }

    @Test
    fun metRepositoryTestGlobalRadiation() = runTest {
        // Arrange
        val station = WeatherStation(id = "SN18700", name = "Oslo")
        val validYears = ValidYears(
            validTo = Instant.parse("2025-01-01T00:00:00Z"),
            validFrom = Instant.parse("2018-01-19T07:46:55.000Z"),
            elementId = "mean(surface_downwelling_shortwave_flux_in_air PT1H)",
            weatherStation = station
        )

        val mockApiResponse = listOf(
            -0.8, -0.85, -0.87, -0.94, -0.93, -0.97, -0.84, -0.76, -0.76, 2.38,
            17.32, 20.0, 12.21, 11.77, 9.66, 0.69, -0.83, -0.81, -0.79, -0.75,
            -0.75, -0.74, -0.81, -0.81
        )

        val mockWeatherList = WeatherList(
            dataList = mockApiResponse.mapIndexed { index, value ->
                WeatherData(
                    elementId = "mean(surface_downwelling_shortwave_flux_in_air PT1H)",
                    value = value,
                    unit = "W/m2",
                    weatherStation = station,
                    timeMeasured = Instant
                        .parse("2024-01-01T00:00:00Z")
                        .plus(index.toLong(), ChronoUnit.HOURS)
                )
            }
        )

        coEvery {
            mockDataSource.getWeatherData(
                station,
                validYears.elementId,
                "2024-01-01",
                "2025-01-01"
            )
        } returns mockWeatherList

        // Act
        val result = repository.getGlobalRadiationData(station, validYears)

        // Assert
        assertNotNull(result)
        assertEquals(1, result!!.dataList.size)
        assertEquals(result.dataList[0].timeMeasured, Instant.parse("2024-01-01T00:00:00Z"))
        val dailyAverage = mockApiResponse.average()
        assertEquals(dailyAverage, result.dataList[0].value, 0.0001)
        assertEquals("W/m2", result.dataList[0].unit)
        assertEquals(
            "mean(surface_downwelling_shortwave_flux_in_air PT1H)",
            result.dataList[0].elementId
        )

        coVerify(exactly = 1) {
            mockDataSource.getWeatherData(
                station,
                validYears.elementId,
                "2024-01-01",
                "2025-01-01"
            )
        }
    }
}
