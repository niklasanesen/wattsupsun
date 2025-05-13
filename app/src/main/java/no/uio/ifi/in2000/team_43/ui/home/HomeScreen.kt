package no.uio.ifi.in2000.team_43.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team_43.ui.pages.MapPage
import no.uio.ifi.in2000.team_43.ui.pages.ProfilePage
import no.uio.ifi.in2000.team_43.ui.theme.Blue
import no.uio.ifi.in2000.team_43.ui.theme.BottomBarColor
import no.uio.ifi.in2000.team_43.ui.theme.Cerulean


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    val navItemList = listOf(
        NavItem("Map", Icons.Default.LocationOn),
        // NavItem("Saved", Icons.Outlined.Favorite),
        NavItem("Profile", Icons.Default.Person),
        // NavItem("Home", Icons.Default.Home)
    )
    var selectedIndex by remember {
        mutableIntStateOf(1)
    }


    // State to track if welcome screen is shown
    val context = LocalContext.current
    var showWelcomeScreen by remember { mutableStateOf(true) }

    if (showWelcomeScreen) {
        WelcomeScreen(
            onGetStartedClick = {
                // Hide welcome screen but don't save preference
                // since we want to show it every time
                showWelcomeScreen = false
            },
            startup = true
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar (
                    containerColor = BottomBarColor,
                ) {
                    navItemList.forEachIndexed { index, navItem ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = {
                                selectedIndex = index
                            },
                            icon = {
                                Icon(
                                    imageVector = navItem.icon,
                                    contentDescription = navItem.label,
                                    tint = Cerulean,
                                    modifier = Modifier
                                        .width(128.dp)
                                        .height(28.dp)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Cerulean,
                                indicatorColor = Blue
                            ),
                        )
                    }
                }
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            ContentScreen(
                modifier = modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                selectedIndex
            )
        }
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int) {
    when(selectedIndex) {
        0 -> MapPage(modifier)
        1 -> ProfilePage(modifier)
    }
}