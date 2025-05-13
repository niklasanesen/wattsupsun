package no.uio.ifi.in2000.team_43.model.panel

data class SunPanel(
    val name: String,
    val price: Int,         // Price in NOK
    val effect: Int,       // Effect in Watt (W)
    val pictureResId: Int,   // Reference to the picture in res/drawable
    val len: Double,
    val width: Double

)
