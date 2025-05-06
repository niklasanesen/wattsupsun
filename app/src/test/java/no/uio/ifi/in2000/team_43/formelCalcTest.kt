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
        val lengde = 10
        val bredde = 5
        val vinkel = 35
        val temperatur = 10.0
        val solinnstraling = 200.0

        // act
        val resultat = beregnStromproduksjon(
            lengde,
            bredde,
            vinkel,
            temperatur,
            solinnstraling
        )

        // assert
        val forventet = 10.0
        assertEquals(forventet, resultat, 0.01)
    }

    @Test
    fun temperaturOverOptimalGirLavereProduksjon() {
        // arrange
        val lengde = 10
        val bredde = 5
        val vinkel = 35
        val temperatur = 35.0
        val solinnstraling = 200.0

        // act
        val resultat = beregnStromproduksjon(
            lengde,
            bredde,
            vinkel,
            temperatur,
            solinnstraling
        )

        // assert
        val forventet = 9.7
        assertEquals(forventet, resultat, 0.01)
    }

    @Test
    fun avvikendeVinkelGirEffekttap() {
        // arrange
        val lengde = 10
        val bredde = 5
        val vinkel = 95
        val temperatur = 25.0
        val solinnstraling = 200.0

        // act
        val resultat = beregnStromproduksjon(
            lengde,
            bredde,
            vinkel,
            temperatur,
            solinnstraling
        )

        // assert
        val forventet = 4.0
        assertEquals(forventet, resultat, 0.01)
    }

    @Test
    fun temperaturOgVinkelGirKombinertEffekttap() {
        // arrange
        val lengde = 10
        val bredde = 5
        val vinkel = 95
        val temperatur = 35.0
        val solinnstraling = 200.0

        // act
        val resultat = beregnStromproduksjon(
            lengde,
            bredde,
            vinkel,
            temperatur,
            solinnstraling
        )

        // assert
        val forventet = 3.88
        assertEquals(forventet, resultat, 0.01)
    }
}