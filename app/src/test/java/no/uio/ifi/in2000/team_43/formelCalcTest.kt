package no.uio.ifi.in2000.team_43

import io.mockk.mockk
import no.uio.ifi.in2000.team_43.data.met.MetRepository
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.met.beregnStromproduksjon

import org.junit.Test
import org.junit.Assert.assertEquals

class FormelTester {

    private val metRepo = mockk<MetRepository>()
    private val metViewModel = MetViewModel(metRepo)

    @Test
    fun beregnSolinnstråling() {
        //arrange
        val skydekke = 8.0
        val snøtykkelse = 90.0
        val globalstråling = 20.0

        //act
        val resultat = metViewModel.calcInsolation(skydekke, snøtykkelse, globalstråling)

        //assert
        val forventet = 3.5
        assertEquals(forventet, resultat, 0.01)
    }

    // Tester strømproduksjonsberegningene:
    // Forventet resultat er forhåndsberegnet på egenhånd.

    @Test
    fun beregnOptimalProduksjon() {
        // arrange
        val areal = 10.0
        val angle = 35
        val temperature = 10.0
        val solinnstraling = 200.0
        val direction = 8

        // act
        val resultat = beregnStromproduksjon(
            areal,
            angle,
            temperature,
            solinnstraling,
            direction,
        )

        // assert
        val forventet = 2.0
        assertEquals(forventet, resultat, 0.01)
    }

    @Test
    fun temperaturOverOptimalGirLavereProduksjon() {
        // arrange
        val areal = 10.0
        val angle = 35
        val temperature = 35.0
        val solinnstraling = 200.0
        val direction = 8

        // act
        val resultat = beregnStromproduksjon(
            areal,
            angle,
            temperature,
            solinnstraling,
            direction
        )

        // assert
        val forventet = 1.94
        assertEquals(forventet, resultat, 0.01)
    }

    @Test
    fun avvikendeVinkelGirEffekttap() {
        // arrange
        val areal = 10.0
        val angle = 95
        val temperature = 25.0
        val solinnstraling = 200.0
        val direction = 8

        // act
        val resultat = beregnStromproduksjon(
            areal,
            angle,
            temperature,
            solinnstraling,
            direction
        )

        // assert
        val forventet = 0.8
        assertEquals(forventet, resultat, 0.01)
    }

    @Test
    fun temperaturOgVinkelGirKombinertEffekttap() {
        // arrange
        val areal = 10.0
        val angle = 95
        val temperature = 35.0
        val solinnstraling = 200.0
        val direction = 8

        // act
        val resultat = beregnStromproduksjon(
            areal,
            angle,
            temperature,
            solinnstraling,
            direction
        )

        // assert
        val forventet = 0.77
        assertEquals(forventet, resultat, 0.01)
    }
}