package no.uio.ifi.in2000.team_43.ui.home

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    )
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        // modifier = Modifier.fillMaxSize(),
        /*topBar = {
            TopAppBar()
        },*/
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = "Icon")
                        }
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        ContentScreen(modifier = modifier.padding(bottom = innerPadding.calculateBottomPadding()), selectedIndex)
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
