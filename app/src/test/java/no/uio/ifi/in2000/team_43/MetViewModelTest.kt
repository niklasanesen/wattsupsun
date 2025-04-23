package no.uio.ifi.in2000.team_43

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import no.uio.ifi.in2000.team_43.data.met.MetRepository
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherData
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.Instant
import app.cash.turbine.test


@OptIn(ExperimentalCoroutinesApi::class)
class MetViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchWeatherData updates data with global radiation if available`() = runTest {
        // Mocked values
        val mockStation = WeatherStation(id = "SN18700", name = "Oslo")
        val mockYears = ValidYears(
            validTo = Instant.parse("2025-01-01T00:00:00Z"),
            elementId = "mean(surface_downwelling_shortwave_flux_in_air PT1H)",
            weatherStation = mockStation
        )
        val radiationData = WeatherList(
            dataList = listOf(
                WeatherData(
                    elementId = "mean(surface_downwelling_shortwave_flux_in_air PT1H)",
                    value = 2.5,
                    unit = "W/m2",
                    weatherStation = mockStation,
                    timeMeasured = Instant.parse("2024-01-01T00:00:00Z")
                )
            )
        )

        // Mock repository
        val mockRepo = mockk<MetRepository>()
        coEvery { mockRepo.getWeatherStation(any()) } returns mockStation
        coEvery { mockRepo.getValidTime(any(), any()) } returns mockYears
        coEvery { mockRepo.getDataSet(any(), any()) } returns radiationData

        val viewModel = MetViewModel(mockRepo)

        viewModel.data.test {
            viewModel.fetchWeatherData(Coordinates(10.6, 59.9))

            val result = awaitItem()
            assertNotNull(result)
            val radiation = result!!.dataList.find { it.elementId.contains("surface_downwelling") }
            assertNotNull(radiation)
            assertEquals(2.5, radiation!!.value, 0.0001)
            assertEquals("W/m2", radiation.unit)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchWeatherData updates data with temperature if available`() = runTest {
        // Mocked values
        val mockStation = WeatherStation(id = "SN18700", name = "Oslo")
        val mockYearsTemp = ValidYears(
            validTo = Instant.parse("2025-01-01T00:00:00Z"),
            elementId = "mean(air_temperature P1Y)",
            weatherStation = mockStation
        )
        val temperatureData = WeatherList(
            dataList = listOf(
                WeatherData(
                    elementId = "mean(air_temperature P1Y)",
                    value = 5.0,
                    unit = "Celsius",
                    weatherStation = mockStation,
                    timeMeasured = Instant.parse("2024-01-01T00:00:00Z")
                )
            )
        )

        // Minimal mocks for cloud and snow to prevent early return
        val mockYearsCloud = mockYearsTemp.copy(elementId = "mean(cloud_area_fraction P1Y)")
        val mockYearsSnow = mockYearsTemp.copy(elementId = "mean(snow_coverage_type P1M)")

        val fakeWeatherList = WeatherList(
            dataList = listOf(
                WeatherData(
                    elementId = "fake",
                    value = 1.0,
                    unit = "fake",
                    weatherStation = mockStation,
                    timeMeasured = Instant.parse("2024-01-01T00:00:00Z")
                )
            )
        )

        // Mock repository
        val mockRepo = mockk<MetRepository>()
        coEvery { mockRepo.getWeatherStation(any()) } returns mockStation
        coEvery { mockRepo.getValidTime(mockStation, "mean(air_temperature P1Y)") } returns mockYearsTemp
        coEvery { mockRepo.getDataSet(mockStation, mockYearsTemp) } returns temperatureData

        // Stubs for required cloud and snow paths
        coEvery { mockRepo.getValidTime(mockStation, "mean(cloud_area_fraction P1Y)") } returns mockYearsCloud
        coEvery { mockRepo.getDataSet(mockStation, mockYearsCloud) } returns fakeWeatherList

        coEvery { mockRepo.getValidTime(mockStation, "mean(snow_coverage_type P1M)") } returns mockYearsSnow
        coEvery { mockRepo.getDataSet(mockStation, mockYearsSnow) } returns fakeWeatherList

        val viewModel = MetViewModel(mockRepo)

        viewModel.data.test {
            viewModel.fetchWeatherData(Coordinates(10.6, 59.9))

            val result = awaitItem()
            println("Received result: $result")
            assertNotNull(result)
            val temperature = result!!.dataList.find { it.elementId == "mean(air_temperature P1Y)" }
            assertNotNull(temperature)

            val expectedAverage = 5.0
            assertEquals(expectedAverage, temperature!!.value, 0.0001)
            assertEquals("Celsius", temperature.unit)
            cancelAndIgnoreRemainingEvents()
        }
    }
}