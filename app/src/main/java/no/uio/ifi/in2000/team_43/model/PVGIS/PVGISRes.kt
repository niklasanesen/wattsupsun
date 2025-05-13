@file:Suppress("PackageName")

package no.uio.ifi.in2000.team_43.model.PVGIS

import com.google.gson.annotations.SerializedName


data class PVGISRes(
    val inputs: Inputs,
    val outputs: Outputs,
    val meta: Meta
)


data class Inputs(
    val location: Location,
    val meteoData: MeteoData,
    val mountingSystem: MountingSystem,
    val pvModule: PVModule,
    val economicData: EconomicData
)


data class Location(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double
)



data class MeteoData(
    val radiationDb: String,
    val meteoDb: String,
    val yearMin: Int,
    val yearMax: Int,
    val useHorizon: Boolean,
    val horizonDb: String
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
    val peakPower: Double,
    val systemLoss: Double
)


data class EconomicData(
    val systemCost: Double?,
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
    val eD: Double,
    val eM: Double,
    @SerializedName("H(i)_d") val hID: Double,
    @SerializedName("H(i)_m") val hIM: Double,
    val sdM: Double
)


data class Totals(
    val fixed: FixedTotals
)

data class FixedTotals(
    val eD: Double,
    val eM: Double,
    val eY: Double,
    val hID: Double,
    val hIM: Double,
    val hIY: Double,
    val sdM: Double,
    val sdY: Double,
    val lAoi: Double,
    val lSpec: String,
    val lTg: Double,
    val lTotal: Double
)


data class Meta(
    val inputs: MetaInputs,
    val outputs: MetaOutputs
)


data class MetaInputs(
    val location: MetaLocation,
    val meteoData: MetaMeteoData,
    val mountingSystem: MetaMountingSystem,
    val pvModule: MetaPVModule,
    val economicData: MetaEconomicData
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