package no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


// Logic for how to show the UI thing:
@Composable
fun totalCostLogic(numPanel: Int, totalPanelPrice: Int, isLoading: Boolean) {
    if (isLoading) {
        CalculatingCardBox("Teller mynter - snart klart!")
    } else {
        // Soory for this bad code
        // pls ask before touching this, might break easly
        val haaland = totalCostAnimated(numPanel, totalPanelPrice)
        if(haaland < 0) {
            CalculatingCardBox("Teller mynter - snart klart!")
        }
    }
}

// The UI componenet:
@Composable
fun totalCostAnimated(numPanel: Int, totalPanelPrice: Int): Int {
    var flipped by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    // This is really really not optimal but it is what it is
    LaunchedEffect(totalPanelPrice) {
        showContent = false
        delay(2000)
        showContent = true
    }

    // pls dont touch this, weird things happen when this is not there
    if (!showContent && totalPanelPrice == 0) {
        return -1
        //CalculatingCardBox("Teller mynter - snart klart!")
    }


    // This is great ngl
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "flip"
    )

    val isZeroSupport = totalPanelPrice <= 0
    val backgroundColor = if (isZeroSupport) Color(0xFFFFE0E0) else Color(0xFFE0F2F1)
    val textColor = if (isZeroSupport) Color(0xFFB00020) else Color(0xFF006064)
    val density = LocalDensity.current.density

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable { flipped = !flipped },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Estimert kostnad for $numPanel paneler",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = textColor
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Mer info",
                            tint = textColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text =
                        "${totalPanelPrice} kr",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .graphicsLayer { rotationY = 180f },
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isZeroSupport) Color(0xFFFFE0E0) else Color(0xFFE0F2F1))
            ) {
                Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Antall paneler og kostnader er estimert ut fra takareal og valgt panelpris. Ta kontakt med fagperson for vurdering av takforhold og endelig pris inkl. installasjon.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isZeroSupport) Color(0xFFB00020) else Color(0xFF006064),
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    return 1
}


