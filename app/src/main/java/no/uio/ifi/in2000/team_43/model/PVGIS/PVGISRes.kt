package no.uio.ifi.in2000.team_43.model.PVGIS

import kotlinx.serialization.Serializable

@Serializable
data class PVGISRes(
    val inputs: Inputs,
    val outputs: Outputs,
    val meta: Meta
)

@Serializable
data class Inputs(
    val location: Location,
    val meteo_data: MeteoData,
    val mounting_system: MountingSystem,
    val pv_module: PVModule,
    val economic_data: EconomicData
)

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double
)

@Serializable
data class MeteoData(
    val radiation_db: String,
    val meteo_db: String,
    val year_min: Int,
    val year_max: Int,
    val use_horizon: Boolean,
    val horizon_db: String
)

@Serializable
data class MountingSystem(
    val fixed: FixedMounting
)

@Serializable
data class FixedMounting(
    val slope: Slope,
    val azimuth: Azimuth,
    val type: String
)

@Serializable
data class Slope(
    val value: Int,
    val optimal: Boolean
)

@Serializable
data class Azimuth(
    val value: Int,
    val optimal: Boolean
)

@Serializable
data class PVModule(
    val technology: String,
    val peak_power: Double,
    val system_loss: Double
)

@Serializable
data class EconomicData(
    val system_cost: Double?,
    val interest: Double?,
    val lifetime: Int?
)

@Serializable
data class Outputs(
    val monthly: MonthlyData,
    val totals: Totals
)

@Serializable
data class MonthlyData(
    val fixed: List<MonthlyEntry>
)

@Serializable
data class MonthlyEntry(
    val month: Int,
    val E_d: Double,
    val E_m: Double,
    val H_i_d: Double,
    val H_i_m: Double,
    val SD_m: Double
)

@Serializable
data class Totals(
    val fixed: FixedTotals
)

@Serializable
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

@Serializable
data class Meta(
    val inputs: MetaInputs,
    val outputs: MetaOutputs
)

@Serializable
data class MetaInputs(
    val location: MetaLocation,
    val meteo_data: MetaMeteoData,
    val mounting_system: MetaMountingSystem,
    val pv_module: MetaPVModule,
    val economic_data: MetaEconomicData
)

@Serializable
data class MetaLocation(
    val description: String,
    val variables: Map<String, MetaVariable>
)

@Serializable
data class MetaMeteoData(
    val description: String,
    val variables: Map<String, MetaVariable>
)

@Serializable
data class MetaMountingSystem(
    val description: String,
    val choices: String,
    val fields: Map<String, MetaVariable>
)

@Serializable
data class MetaPVModule(
    val description: String,
    val variables: Map<String, MetaVariable>
)

@Serializable
data class MetaEconomicData(
    val description: String,
    val variables: Map<String, MetaVariable>
)

@Serializable
data class MetaVariable(
    val description: String,
    val units: String?
)

@Serializable
data class MetaOutputs(
    val monthly: MetaMonthly,
    val totals: MetaTotals
)

@Serializable
data class MetaMonthly(
    val type: String,
    val timestamp: String,
    val variables: Map<String, MetaVariable>
)

@Serializable
data class MetaTotals(
    val type: String,
    val variables: Map<String, MetaVariable>
)
