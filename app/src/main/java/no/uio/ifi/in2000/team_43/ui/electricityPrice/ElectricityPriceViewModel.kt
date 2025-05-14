package no.uio.ifi.in2000.team_43.ui.electricityPrice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.data.electricityPrice.ElectricityPriceDataSource
import no.uio.ifi.in2000.team_43.data.electricityPrice.ElectricityPriceRepository
import no.uio.ifi.in2000.team_43.model.electricityDistrict.ElectricityDistrict
import no.uio.ifi.in2000.team_43.model.electricityPrice.ElectricityPriceRes

class ElectricityPriceViewModel : ViewModel() {

    private val electricityPriceRepo = ElectricityPriceRepository(electricityPriceDataSource = ElectricityPriceDataSource())

    private val _data = MutableStateFlow<List<ElectricityPriceRes>?>(null)
    val data: StateFlow<List<ElectricityPriceRes>?> = _data.asStateFlow()

    private val _dataPerMnd = MutableStateFlow<List<List<ElectricityPriceRes>?>?>(null)
    val dataPerMnd: StateFlow<List<List<ElectricityPriceRes>?>?> = _dataPerMnd.asStateFlow()

    fun fetchElectricityData(electricityDistrict: ElectricityDistrict?) {
        viewModelScope.launch {
            try {
                Log.i("TestElectricityPrice", "Fetching electricity price data from API...")
                val response = electricityPriceRepo.getElectricityPriceList(electricityDistrict)
                _data.value = response
            } catch (e: Exception) {
                Log.e("TestElectricityPrice", "Error when fetching electricity price data: ${e.message}")
            }


            try {
                Log.i("TestElectricityPrice", "fetching electricity data per month... (viewmodel)")
                val responsePerMnd = electricityPriceRepo.getElectricityPricePerMonth(electricityDistrict)
                _dataPerMnd.value = responsePerMnd
            } catch (e: Exception) {
                Log.e("TestElectricityPrice", "Error when fetching electricity price data per month: ${e.message}")
            }
        }
    }
}
