package no.uio.ifi.in2000.team_43

import no.uio.ifi.in2000.team_43.data.electricityPrice.ElectricityPriceDataSource
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team_43.data.electricityPrice.ElectricityPriceRepository
import no.uio.ifi.in2000.team_43.model.electricityDistrict.ElectricityDistrict
import no.uio.ifi.in2000.team_43.model.electricityPrice.ElectricityPriceRes
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ElectricityPriceRepoTest {

    private val mockDataSource: ElectricityPriceDataSource = mock()
    private val repository = ElectricityPriceRepository(mockDataSource)

    @Test
    fun `calculateAveragePrice should return average of 24 hourly prices`() = runTest {
        // Arrange: Mock 24 hours with varying prices
        val testData = (0 until 24).map { hour ->
            ElectricityPriceRes(
                nokPerKWh = if (hour < 12) 20.0 else 10.0, // First 12 hours: 20 kr, rest: 10 kr
                eurPerKWh = 0.1,  // Not relevant for test
                exr = 10.0,         // Not relevant
                timeStart = "2023-01-01T${hour.toString().padStart(2, '0')}:00:00Z",
                timeEnd = "2023-01-01T${(hour + 1).toString().padStart(2, '0')}:00:00Z"
            )
        }

        whenever(mockDataSource.getElectricityPriceData(ElectricityDistrict.NO1)).thenReturn(testData)

        // Act: Calls function (that doesn't exist yet -> test will fail)
        val result = repository.calculateAveragePrice(ElectricityDistrict.NO1)

        // Assert: Expected average is sum(alle prices)/24
        val expectedAverage = 15.0
        assertEquals(expectedAverage, result, 0.0001)
    }

    @Test
    fun `calculateAveragePrice should return 0 if data is null`() = runTest {
        // Arrange
        whenever(mockDataSource.getElectricityPriceData(ElectricityDistrict.NO1)).thenReturn(null)

        // Act
        val result = repository.calculateAveragePrice(ElectricityDistrict.NO1)

        // Assert
        assertEquals(0.0, result, 0.0)
    }
}