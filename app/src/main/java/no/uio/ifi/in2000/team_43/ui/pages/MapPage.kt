package no.uio.ifi.in2000.team_43.ui.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.home.HomeViewModel
import no.uio.ifi.in2000.team_43.ui.home.MapScreen
import no.uio.ifi.in2000.team_43.ui.info.InfoScreen
import no.uio.ifi.in2000.team_43.ui.input.InputScreen
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.satellite.SatelliteScreen
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import no.uio.ifi.in2000.team_43.ui.popUpHelper.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import no.uio.ifi.in2000.team_43.ui.theme.Blue
import no.uio.ifi.in2000.team_43.ui.theme.BottomBarColor
import no.uio.ifi.in2000.team_43.ui.theme.Cerulean
import no.uio.ifi.in2000.team_43.ui.theme.DarkSlate
import no.uio.ifi.in2000.team_43.ui.theme.TopBarColor

/**
 * enum values that represent the screens in the app
 */
enum class HomeScreen(val route: String) {
    Start("Kart"),
    Input("Registrer takflate"),
    Info("Info"),
    Satellite("Tegn takflate")
}

@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    metViewModel: MetViewModel = viewModel(),
    pvgisViewModel: PVGISViewModel = viewModel(),
    viewModel: HomeViewModel = viewModel(),
    navController: NavHostController = rememberNavController()

) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()

    // Get the name of the current screen
    val currentScreen = HomeScreen.valueOf(
        backStackEntry?.destination?.route ?: HomeScreen.Start.name
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        },
    ) { innerPadding ->
        val ctx by viewModel.ctx.collectAsState()

        NavHost(
            navController = navController,
            startDestination = HomeScreen.Start.name,
            modifier = Modifier
                .fillMaxSize()
        ) {
            composable(route = HomeScreen.Start.name) {
                viewModel.resetCtx()
                MapScreen(
                    onInputBtnClicked = { coords, address, stromdistrikt ->
                        viewModel.setCoords(coords)
                        viewModel.setAddress(address)
                        viewModel.setStromdistrikt(stromdistrikt)
                        navController.navigate(HomeScreen.Input.name)
                    },
                    onSatelliteBtnClicked = { coords, address, stromdistrikt ->
                        viewModel.setCoords(coords)
                        viewModel.setAddress(address)
                        viewModel.setStromdistrikt(stromdistrikt)
                        navController.navigate(HomeScreen.Satellite.name)
                    }
                )
            }
            composable(route = HomeScreen.Input.name) {
                InputScreen(
                    ctx = ctx,
                    metViewModel = metViewModel,
                    pvgisViewModel = pvgisViewModel,
                    onNextBtnClicked = {
                        viewModel.setInput(it)
                        navController.navigate(HomeScreen.Info.name)
                    },
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                )
            }
            composable(route = HomeScreen.Satellite.name) {
                SatelliteScreen(
                    ctx = ctx,
                    onNextBtnClicked = {
                        viewModel.setArea(it)
                        navController.navigate(HomeScreen.Input.name)
                    },
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                )
            }

            composable(route = HomeScreen.Info.name) {
                InfoScreen(
                    ctx = ctx,
                    metViewModel = metViewModel,
                    pvgisViewModel = pvgisViewModel,
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    currentScreen: HomeScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    var showHelp by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = currentScreen.route,
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = TopBarColor,
            titleContentColor = Color.White,
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Tilbake"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { showHelp = true }) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Hjelp",
                    tint = DarkSlate
                )
            }
        }
    )

    if (showHelp) {
        when (currentScreen) {
            HomeScreen.Start -> HelpOnMapScreen { showHelp = false }
            HomeScreen.Input -> HelpOnInputScreen { showHelp = false }
            HomeScreen.Info -> HelpOnInfoScreen { showHelp = false }
            HomeScreen.Satellite -> HelpOnSateliteScreen { showHelp = false }
        }
    }
}
