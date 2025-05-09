package no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations


fun calculateNumPanels(
    roofLength: Double?,
    roofWidth: Double?,
    panelLength: Double?,
    panelWidth: Double?,
): Int {

    if (roofLength == null || roofWidth == null || panelLength == null || panelWidth == null) {
        return 0
    }


    val panelsAlongLength: Int = (roofLength / panelLength).toInt()
    val panelsAlongWidth: Int = (roofWidth / panelWidth).toInt()

    val totalThatFit: Int = panelsAlongLength * panelsAlongWidth

    return totalThatFit
}