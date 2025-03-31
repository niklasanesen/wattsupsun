package no.uio.ifi.in2000.team_43.model.DRcalcRes

import com.google.gson.annotations.SerializedName

data class DRcalcRes(
    val inputs: Inputs,
    val outputs: Outputs,
    val meta: Meta
)

data class Inputs(
    val location: Location,
    val meteo_data: MeteoData,
    val plane: Plane,
    val time_format: String
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val elevation: Int
)

data class MeteoData(
    val radiation_db: String,
    val meteo_db: String,
    val year_min: Int,
    val year_max: Int,
    val use_horizon: Boolean,
    val horizon_db: String
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
    val daily_profile: List<DailyProfile>
)

data class DailyProfile(
    val month: Int,
    val time: String,
    @SerializedName("G(i)") val G_i: Double,   // Tidligere `G(i)`
    @SerializedName("Gb(i)") val Gb_i: Double,  // Tidligere `Gb(i)`
    @SerializedName("Gd(i)") val Gd_i: Double   // Tidligere `Gd(i)`
)

data class Meta(
    val inputs: MetaInputs,
    val outputs: MetaOutputs
)

data class MetaInputs(
    val location: MetaLocation,
    val meteo_data: MetaMeteoData,
    val plane: MetaPlane,
    val time_format: List<MetaDescription>
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
    val radiation_db: MetaVariable,
    val meteo_db: MetaVariable,
    val year_min: MetaVariable,
    val year_max: MetaVariable,
    val use_horizon: MetaVariable,
    val horizon_db: MetaVariable
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
    val daily_profile: DailyProfileMeta
)

data class DailyProfileMeta(
    val type: String,
    val timestamp: String,
    val variables: ProfileVariables
)

data class ProfileVariables(
    val G_i: MetaVariable,   // Renamed from `G(i)`
    val Gb_i: MetaVariable,  // Renamed from `Gb(i)`
    val Gd_i: MetaVariable   // Renamed from `Gd(i)`
)

data class MetaDescription(
    val description: String
)
