package no.uio.ifi.in2000.team_43.ui.home

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import no.uio.ifi.in2000.team_43.ui.pages.MapPage
import no.uio.ifi.in2000.team_43.ui.pages.ProfilePage
import no.uio.ifi.in2000.team_43.ui.pages.SavedPage



@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    val navItemList = listOf(
        NavItem("Map", Icons.Default.LocationOn),
        NavItem("Saved", Icons.Outlined.Favorite),
        NavItem("Profile", Icons.Default.Person),
        NavItem("Home", Icons.Default.Home)
    )
    var selectedIndex by remember {
        mutableIntStateOf(0)
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
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    navItemList.forEachIndexed { index, navItem ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = {
                                if (index == 3) { // Home button
                                    showWelcomeScreen = true
                                } else {
                                    selectedIndex = index
                                }
                            },
                            icon = {
                                Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                            }
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
        1 -> SavedPage(modifier)
        2 -> ProfilePage(modifier)
    }
}