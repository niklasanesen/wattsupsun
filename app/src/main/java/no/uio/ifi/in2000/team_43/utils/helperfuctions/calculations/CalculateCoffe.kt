package no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations

fun calculateCoffe(kWhProduced: Double): Int {
    val kWhPerCup = 0.16
    val totCoffeeCups = (kWhProduced / kWhPerCup).toInt()
    return totCoffeeCups
}