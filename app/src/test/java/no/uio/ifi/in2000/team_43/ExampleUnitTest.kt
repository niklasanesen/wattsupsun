package no.uio.ifi.in2000.team_43

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team_43.data.met.MetDataSource
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
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
}