package no.uio.ifi.in2000.team_43

import no.uio.ifi.in2000.team_43.data.stromPris.StromprisDataSource
import no.uio.ifi.in2000.team_43.data.stromPris.StromprisRepository

import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team_43.model.stromPris.StromprisRes
import no.uio.ifi.in2000.team_43.model.stromdistrikt.StromDistrikt
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Ignore

class StromprisRepoTest {

    private val mockDataSource: StromprisDataSource = mock()
    private val repository = StromprisRepository(mockDataSource)

    @Test
    fun `calculateAveragePrice should return average of 24 hourly prices`() = runTest {
        // Arrange: Mock 24 timer med varierende priser
        val testData = (0 until 24).map { hour ->
            StromprisRes(
                NOK_per_kWh = if (hour < 12) 20.0 else 10.0, // Første 12 timer: 20 kr, resten: 10 kr
                EUR_per_kWh = 0.1,  // Ikke relevant for testen
                EXR = 10.0,         // Ikke relevant
                time_start = "2023-01-01T${hour.toString().padStart(2, '0')}:00:00Z",
                time_end = "2023-01-01T${(hour + 1).toString().padStart(2, '0')}:00:00Z"
            )
        }

        whenever(mockDataSource.getStromprisData(StromDistrikt.NO1)).thenReturn(testData)

        // Act: Kaller funksjonen (som ikke finnes ennå -> test vil feile)
        val result = repository.calculateAveragePrice(StromDistrikt.NO1)

        // Assert: Forventet snitt er sum(alle priser)/24
        val expectedAverage = 15.0
        assertEquals(expectedAverage, result, 0.0001)
    }

    @Test
    fun `calculateAveragePrice should return 0 if data is null`() = runTest {
        // Arrange
        whenever(mockDataSource.getStromprisData(StromDistrikt.NO1)).thenReturn(null)

        // Act
        val result = repository.calculateAveragePrice(StromDistrikt.NO1)

        // Assert
        assertEquals(0.0, result, 0.0)
    }
}