package no.uio.ifi.in2000.team_43.model.input

import no.uio.ifi.in2000.team_43.model.panel.SunPanel

data class Input (
    val length: Int,
    val width: Int,
    val angle: Int,
    val direction: Int,
    val valgtPanle: SunPanel
)