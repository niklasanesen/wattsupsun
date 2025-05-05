package no.uio.ifi.in2000.team_43.ui.home

import androidx.lifecycle.ViewModel
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.team_43.model.areal.Areal
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.input.Input
import no.uio.ifi.in2000.team_43.model.stromdistrikt.StromDistrikt

class HomeViewModel() : ViewModel() {
    /**
     * app state
     */
    private val _ctx = MutableStateFlow<Ctx>(Ctx(coords = null, input = null, areal = null, address = null, stromdistrikt = null))
    val ctx: StateFlow<Ctx> = _ctx.asStateFlow()

    fun resetCtx() {
        // _ctx.value = Ctx(coords = null, input = null, areal = null)
        _ctx.update { currentState ->
            currentState.copy(
                input = null,
                areal = null
            )
        }
    }

    fun setCoords(newCoords: Point?) {
        _ctx.update { currentState ->
            currentState.copy(
                coords = newCoords,
            )
        }
    }

    fun setInput(newInput: Input) {
        _ctx.update { currentState ->
            currentState.copy(
                input = newInput
            )
        }
    }

    fun setArea(newAreal: Areal) {
        _ctx.update { currentState ->
            currentState.copy(
                areal = newAreal
            )
        }
    }

    fun setAddress(newAddress: String?) {
        _ctx.update { currentState ->
            currentState.copy(
                address = newAddress
            )
        }
    }

    fun setStromdistrikt(newStromdistrikt: StromDistrikt?) {
        _ctx.update { currentState ->
            currentState.copy(
                stromdistrikt = newStromdistrikt
            )
        }
    }
}
