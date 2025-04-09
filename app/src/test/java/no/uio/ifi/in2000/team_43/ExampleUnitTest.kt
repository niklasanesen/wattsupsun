package no.uio.ifi.in2000.team_43

import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team_43.data.met.MetDataSource
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    val coordinates = Coordinates(
        latitude = 10.6,
        longitude = 59.9
    )
    @Test
    fun weatherData_isCorrect() = runTest {
        val metDataSource = MetDataSource()

        val station = metDataSource.getWeatherStationData(coordinates)

        assertEquals(4, 2 + 2)
    }
}