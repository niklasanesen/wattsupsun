package no.uio.ifi.in2000.team_43

import no.uio.ifi.in2000.team_43.ui.met.beregnStromproduksjon


import org.junit.Test
import org.junit.Assert.assertEquals


class FormelTester {

    val x = (0..100).random()

    @Test
    fun beregnSolinnstråling() {
        //arrange
        val skydekke = 8
        val snøtykkelse = 90
        val globalstråling = 20
      
         //act
         val resultat = calcInsolation(skydekke, snøtykkelse, globalstråling)
         
         //assert
         val forventet = 3.5
         assertEquals(forventet, resultat, 0.01)
    }

    // Tester strømproduksjonsberegningene:
    // Forventet resultat er forhåndsberegnet på egenhånd.

    @Test
    fun beregnOptimalProduksjon() {
        // arrange
        val solinnstraling = 1000.0
        val areal = 10.0
        val temperatur = 25.0
        val anbefaltVinkel = true
        val avvikVinkel = 0.0


        // act
        val resultat = beregnStromproduksjon(
            solinnstraling,
            areal,
            temperatur,
            anbefaltVinkel,
            avvikVinkel
        )

        // assert
        val forventet = 10000.0
        assertEquals(forventet, resultat, 0.01)
    }


    @Test
    fun temperaturOverOptimalGirLavereProduksjon() {

        // arrange
        val solinnstraling = 1000.0
        val areal = 10.0
        val temperatur = 35.0
        val anbefaltVinkel = true
        val avvikVinkel = 0.0


        // act
        val resultat = beregnStromproduksjon(
            solinnstraling,
            areal,
            temperatur,
            anbefaltVinkel,
            avvikVinkel
        )


        // assert
        val forventet = 9700.0
        assertEquals(forventet, resultat, 0.01)
    }

    @Test
    fun avvikendeVinkelGirEffekttap() {

        // arrange
        val solinnstraling = 1000.0
        val areal = 10.0
        val temperatur = 25.0
        val anbefaltVinkel = false
        val avvikVinkel = 60.0

        // act
        val resultat = beregnStromproduksjon(
            solinnstraling,
            areal,
            temperatur,
            anbefaltVinkel,
            avvikVinkel
        )


        // assert
        val forventet = 5000.0
        assertEquals(forventet, resultat, 0.01)
    }


    @Test
    fun temperaturOgVinkelGirKombinertEffekttap() {
        // arrange
        val solinnstraling = 1000.0
        val areal = 10.0
        val temperatur = 35.0
        val anbefaltVinkel = false
        val avvikVinkel = 60.0


        // act
        val resultat = beregnStromproduksjon(
            solinnstraling,
            areal,
            temperatur,
            anbefaltVinkel,
            avvikVinkel
        )


        // assert
        val forventet = 4850.0
        assertEquals(forventet, resultat, 0.01)
    }
}
