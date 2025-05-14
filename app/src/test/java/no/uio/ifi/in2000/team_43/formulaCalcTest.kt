package no.uio.ifi.in2000.team_43

import io.mockk.mockk
import no.uio.ifi.in2000.team_43.data.met.MetRepository
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.met.calculateElectricityProduction

import org.junit.Test
import org.junit.Assert.assertEquals

class FormulaTests {

    private val metRepo = mockk<MetRepository>()
    private val metViewModel = MetViewModel(metRepo)

    @Test
    fun calculateSolarInsolation() {
        //arrange
        val cloudCover = 8.0
        val snowThickness = 90.0
        val globalRadiation = 20.0

        //act
        val result = metViewModel.calcInsolation(cloudCover, snowThickness, globalRadiation)

        //assert
        val expected = 3.5
        assertEquals(expected, result, 0.01)
    }

    // Tester electricity production calculations:
    // Expected result is calculated in advance by us.

    @Test
    fun calculateOptimalProduction() {
        // arrange
        val areal = 10.0
        val angle = 35
        val temperature = 10.0
        val solarInsolation = 200.0
        val direction = 8

        // act
        val result = calculateElectricityProduction(
            areal,
            angle,
            temperature,
            solarInsolation,
            direction,
        )

        // assert
        val expected = 2.0
        assertEquals(expected, result, 0.01)
    }

    @Test
    fun temperatureOverOptimalGivesLowerProduction() {
        // arrange
        val areal = 10.0
        val angle = 35
        val temperature = 35.0
        val solarInsolation = 200.0
        val direction = 8

        // act
        val result = calculateElectricityProduction(
            areal,
            angle,
            temperature,
            solarInsolation,
            direction
        )

        // assert
        val expected = 1.94
        assertEquals(expected, result, 0.01)
    }

    @Test
    fun deviatingAngleGivesEffectLoss() {
        // arrange
        val areal = 10.0
        val angle = 95
        val temperature = 25.0
        val solarInsolation = 200.0
        val direction = 8

        // act
        val result = calculateElectricityProduction(
            areal,
            angle,
            temperature,
            solarInsolation,
            direction
        )

        // assert
        val expected = 0.8
        assertEquals(expected, result, 0.01)
    }

    @Test
    fun temperatureAndAngleGiveCombinedEffectLoss() {
        // arrange
        val areal = 10.0
        val angle = 95
        val temperature = 35.0
        val solarInsolation = 200.0
        val direction = 8

        // act
        val result = calculateElectricityProduction(
            areal,
            angle,
            temperature,
            solarInsolation,
            direction
        )

        // assert
        val expected = 0.77
        assertEquals(expected, result, 0.01)
    }
}