package no.uio.ifi.in2000.team_43.model.PVGIS

import com.google.gson.annotations.SerializedName


data class PVGISRes(
    val inputs: Inputs,
    val outputs: Outputs,
    val meta: Meta
)


data class Inputs(
    val location: Location,
    val meteo_data: MeteoData,
    val mounting_system: MountingSystem,
    val pv_module: PVModule,
    val economic_data: EconomicData
)


data class Location(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double
)



data class MeteoData(
    val radiation_db: String,
    val meteo_db: String,
    val year_min: Int,
    val year_max: Int,
    val use_horizon: Boolean,
    val horizon_db: String
)


data class MountingSystem(
    val fixed: FixedMounting
)


data class FixedMounting(
    val slope: Slope,
    val azimuth: Azimuth,
    val type: String
)


data class Slope(
    val value: Int,
    val optimal: Boolean
)

data class Azimuth(
    val value: Int,
    val optimal: Boolean
)


data class PVModule(
    val technology: String,
    val peak_power: Double,
    val system_loss: Double
)


data class EconomicData(
    val system_cost: Double?,
    val interest: Double?,
    val lifetime: Int?
)


data class Outputs(
    val monthly: MonthlyData,
    val totals: Totals
)


data class MonthlyData(
    val fixed: List<MonthlyEntry>
)

data class MonthlyEntry(
    val month: Int,
    val E_d: Double,
    val E_m: Double,
    @SerializedName("H(i)_d") val H_i_d: Double,
    @SerializedName("H(i)_m") val H_i_m: Double,
    val SD_m: Double
)


data class Totals(
    val fixed: FixedTotals
)

data class FixedTotals(
    val E_d: Double,
    val E_m: Double,
    val E_y: Double,
    val H_i_d: Double,
    val H_i_m: Double,
    val H_i_y: Double,
    val SD_m: Double,
    val SD_y: Double,
    val l_aoi: Double,
    val l_spec: String,
    val l_tg: Double,
    val l_total: Double
)


data class Meta(
    val inputs: MetaInputs,
    val outputs: MetaOutputs
)


data class MetaInputs(
    val location: MetaLocation,
    val meteo_data: MetaMeteoData,
    val mounting_system: MetaMountingSystem,
    val pv_module: MetaPVModule,
    val economic_data: MetaEconomicData
)

data class MetaLocation(
    val description: String,
    val variables: Map<String, MetaVariable>
)


data class MetaMeteoData(
    val description: String,
    val variables: Map<String, MetaVariable>
)

data class MetaMountingSystem(
    val description: String,
    val choices: String,
    val fields: Map<String, MetaVariable>
)


data class MetaPVModule(
    val description: String,
    val variables: Map<String, MetaVariable>
)


data class MetaEconomicData(
    val description: String,
    val variables: Map<String, MetaVariable>
)


data class MetaVariable(
    val description: String,
    val units: String?
)

data class MetaOutputs(
    val monthly: MetaMonthly,
    val totals: MetaTotals
)


data class MetaMonthly(
    val type: String,
    val timestamp: String,
    val variables: Map<String, MetaVariable>
)


data class MetaTotals(
    val type: String,
    val variables: Map<String, MetaVariable>
)