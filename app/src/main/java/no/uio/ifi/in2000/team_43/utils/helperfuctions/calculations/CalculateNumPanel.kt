package no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations


fun calculateNumPanels(
    roofLength: Double?,     // in meters
    roofWidth: Double?,      // in meters
    panelLength: Double?,    // in meters
    panelWidth: Double?,     // in meters
): Int {
    // Null-check at the beginning
    if (roofLength == null || roofWidth == null || panelLength == null || panelWidth == null) {
        return 0 // or throw IllegalArgumentException("All inputs must be non-null")
    }
    // Number of panels that fit along each direction

    val panelsAlongLength: Int = (roofLength / panelLength).toInt()
    val panelsAlongWidth: Int = (roofWidth / panelWidth).toInt()

    val totalThatFit: Int = panelsAlongLength * panelsAlongWidth

    return totalThatFit
}