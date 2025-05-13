package no.uio.ifi.in2000.team_43

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import no.uio.ifi.in2000.team_43.data.met.MetRepository
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherData
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class MetViewModelTest {

    private val metRepo = mockk<MetRepository>()
    private val metViewModel = MetViewModel(metRepo)

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val dispatcher = StandardTestDispatcher()


    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testDataSortedByDate() = runTest {
        // Arrange
        val coordinates = Coordinates(latitude = 59.9, longitude = 10.72)

        val station = WeatherStation(
            id = "SN18700",
            name = "OSLO - BLINDERN",
            distance = 8.17602469651
        )

        val validYears = ValidYears(
            validFrom = Instant.parse("2024-01-01T00:00:00Z"),
            validTo = Instant.parse("2025-01-01T00:00:00Z"),
            elementId = "",
            weatherStation = station
        )

        val tempData = WeatherData(
            elementId = "mean(air_temperature P1D)",
            value = 1.0,
            unit = "C",
            weatherStation = station,
            timeMeasured = Instant.parse("2024-01-01T00:00:00Z")
        )
        val cloudData = WeatherData(
            elementId = "mean(cloud_area_fraction P1D)",
            value = 5.0,
            unit = "octa",
            weatherStation = station,
            timeMeasured = Instant.parse("2024-01-01T00:00:00Z")
        )
        val snowData = WeatherData(
            elementId = "mean(surface_snow_thickness P1M)",
            value = 2.0,
            unit = "cm",
            weatherStation = station,
            timeMeasured = Instant.parse("2024-01-01T00:00:00Z")
        )
        val radiationData = WeatherData(
            elementId = "mean(surface_downwelling_shortwave_flux_in_air PT1H)",
            value = 5.0,
            unit = "W/m2",
            weatherStation = station,
            timeMeasured = Instant.parse("2024-01-01T00:00:00Z")
        )

        val mockWeatherList = WeatherList(listOf(tempData, cloudData, snowData, radiationData))

        coEvery { metRepo.getWeatherStation(coordinates, any()) } returns station

        coEvery { metRepo.getValidTime(any()) } returns validYears

        coEvery { metRepo.getDataSet(any(), any()) } returns mockWeatherList
        coEvery { metRepo.getSnowThickness(any(), any()) } returns mockWeatherList
        coEvery { metRepo.getGlobalRadiationData(any(), any()) } returns mockWeatherList

        // Act
        metViewModel.fetchWeatherData(coordinates)
        dispatcher.scheduler.advanceUntilIdle()

        // Use collect to wait for the value to be updated in the flow
        val dataSortedByDate = metViewModel.dataSortedByDate.first { it != null }// This will block until an item is emitted

            // Assert that dataSortedByDate is not null
            assertNotNull(dataSortedByDate)

            // Check if the dailyObservation list is null or empty
            val dailyObservation = dataSortedByDate?.dailyObservation
            assertNotNull(dailyObservation)
            assertTrue(dailyObservation!!.isNotEmpty())

            // Log the contents of dailyObservation for debugging
            println("dailyObservation: $dailyObservation")

            // Check that data is sorted by date
            val firstItem = dailyObservation.first()
            assertNotNull(firstItem)
            println("First item: $firstItem")

            // Log the value of the first item for debugging
            firstItem.let {
                println("First item date: ${it.first}")
                println("First item data: ${it.second.dataList}")
            }

            // Assert that the first item's date is as expected
            assertEquals(Instant.parse("2024-01-01T00:00:00Z"), firstItem.first)

            // Validate that the calculated insolation for the given day is correctly added
            val insolationData = firstItem.second.dataList.last()
            assertNotNull(insolationData)
            println("Insolation data: $insolationData")

            assertEquals("mean(solar_irradiance P1D)", insolationData.elementId)
            assertEquals("W/m2", insolationData.unit)

            // Validate error message state if necessary
            coVerify { metRepo.getWeatherStation(coordinates, 1) }
            coVerify { metRepo.getValidTime(any()) }
            coVerify { metRepo.getDataSet(any(), any()) }
            coVerify { metRepo.getSnowThickness(any(), any()) }
            coVerify { metRepo.getGlobalRadiationData(any(), any()) }
        }
}