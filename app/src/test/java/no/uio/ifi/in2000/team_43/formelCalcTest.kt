package no.uio.ifi.in2000.team_43

import kotlin.math.min
import no.uio.ifi.in2000.team_43.ui.met.calcCCF
import no.uio.ifi.in2000.team_43.ui.met.calcSCF
import no.uio.ifi.in2000.team_43.ui.met.calcGHI


import org.junit.Test
import org.junit.Assert.assertEquals


class FormelTester {

    val x = (0..100).random()

    @Test
    fun testCalcCCF() {

        //arrange
        val expected = 1 - (x.toDouble() / 8) * 0.75

        //act
        val actual = calcCCF(x)

        //assert
        assertEquals(expected, actual, 0.0001)
    }

    @Test
    fun testCalcSCF() {
        //arrange
        val expected = 1 - min(x.toDouble() / 30, 1.0) * 0.3

        //act
        val actual = calcSCF(x)

        //assert
        assertEquals(expected, actual, 0.0001)
    }

    @Test
    fun testCalcGHI() {
        //arrange
        val y = 2
        val z = 3
        val expected = (x * y * z).toDouble()

        //act
        val actual = calcGHI(x, y, z)

        //assert
        assertEquals(expected, actual, 0.0001)
    }
}