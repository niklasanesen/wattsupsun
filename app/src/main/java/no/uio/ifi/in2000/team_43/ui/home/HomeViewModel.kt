package no.uio.ifi.in2000.team_43.ui.home

import androidx.lifecycle.ViewModel
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.team_43.model.area.Area
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.electricityDistrict.ElectricityDistrict
import no.uio.ifi.in2000.team_43.model.input.Input

class HomeViewModel() : ViewModel() {
    /**
     * app state
     */
    private val _ctx = MutableStateFlow<Ctx>(Ctx(coordinates = null, input = null, area = null, address = null, electricityDistrict = null))
    val ctx: StateFlow<Ctx> = _ctx.asStateFlow()

    fun resetCtx() {
        // _ctx.value = Ctx(coordinates = null, input = null, area = null)
        _ctx.update { currentState ->
            currentState.copy(
                input = null,
                area = null
            )
        }
    }

    fun setCoordinates(newCoordinates: Point?) {
        _ctx.update { currentState ->
            currentState.copy(
                coordinates = newCoordinates,
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

    fun setArea(newArea: Area) {
        _ctx.update { currentState ->
            currentState.copy(
                area = newArea
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

    fun setElectricityDistrict(newElectricityDistrict: ElectricityDistrict?) {
        _ctx.update { currentState ->
            currentState.copy(
                electricityDistrict = newElectricityDistrict
            )
        }
    }
}
