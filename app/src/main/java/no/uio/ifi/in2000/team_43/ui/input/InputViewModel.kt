package no.uio.ifi.in2000.team_43.ui.input

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import no.uio.ifi.in2000.team_43.model.input.Direction
import no.uio.ifi.in2000.team_43.model.panel.SunPanel

class InputViewModel : ViewModel() {
    var length by mutableStateOf("")
    var width by mutableStateOf("")
    var angle by mutableStateOf("")
    var direction by mutableStateOf(Direction.NORTH)
    var selectedPanel by mutableStateOf<SunPanel?>(null)
}