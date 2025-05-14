@file:Suppress("PackageName")

package no.uio.ifi.in2000.team_43.model.DRcalcRes

import com.google.gson.annotations.SerializedName

data class DRcalcRes(
    val inputs: Inputs,
    val outputs: Outputs,
    val meta: Meta
)

data class Inputs(
    val location: Location,
    val meteoData: MeteoData,
    val plane: Plane,
    val timeFormat: String
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val elevation: Int
)

data class MeteoData(
    val radiationDb: String,
    val meteoDb: String,
    val yearMin: Int,
    val yearMax: Int,
    val useHorizon: Boolean,
    val horizonDb: String
)

data class Plane(
    val fixed: Fixed
)

data class Fixed(
    val slope: Angle,
    val azimuth: Angle
)

data class Angle(
    val value: Double,
    val optimal: Boolean
)

data class Outputs(
    val dailyProfile: List<DailyProfile>
)

data class DailyProfile(
    val month: Int,
    val time: String,
    @SerializedName("G(i)") val gI: Double,
    @SerializedName("Gb(i)") val gbI: Double,
    @SerializedName("Gd(i)") val gdI: Double
)

data class Meta(
    val inputs: MetaInputs,
    val outputs: MetaOutputs
)

data class MetaInputs(
    val location: MetaLocation,
    val meteoData: MetaMeteoData,
    val plane: MetaPlane,
    val timeFormat: List<MetaDescription>
)

data class MetaLocation(
    val description: String,
    val variables: LocationVariables
)

data class LocationVariables(
    val latitude: MetaVariable,
    val longitude: MetaVariable,
    val elevation: MetaVariable
)

data class MetaVariable(
    val description: String,
    val units: String? = null
)

data class MetaMeteoData(
    val description: String,
    val variables: MeteoVariables
)

data class MeteoVariables(
    val radiationDb: MetaVariable,
    val meteoDb: MetaVariable,
    val yearMin: MetaVariable,
    val yearMax: MetaVariable,
    val useHorizon: MetaVariable,
    val horizonDb: MetaVariable
)

data class MetaPlane(
    val description: String,
    val fields: PlaneFields
)

data class PlaneFields(
    val slope: MetaVariable,
    val azimuth: MetaVariable
)

data class MetaOutputs(
    val dailyProfile: DailyProfileMeta
)

data class DailyProfileMeta(
    val type: String,
    val timestamp: String,
    val variables: ProfileVariables
)

data class ProfileVariables(
    val gI: MetaVariable,
    val gbI: MetaVariable,
    val gdI: MetaVariable
)

data class MetaDescription(
    val description: String
)
