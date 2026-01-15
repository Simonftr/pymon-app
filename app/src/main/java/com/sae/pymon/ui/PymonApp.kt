package com.sae.pymon.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sae.pymon.ui.screens.GameViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sae.pymon.ui.screens.ConnectViewModel
import com.sae.pymon.ui.screens.GameScreen
import com.sae.pymon.ui.screens.LoginScreen


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Game : Screen("game")
}
@Composable
fun PymonApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold() { innerPadding ->


        NavHost(
            navController = navController,
            startDestination = Screen.Login.route
        )
        {
            composable(Screen.Login.route) {
                val connectViewModel: ConnectViewModel =
                    viewModel(factory = ConnectViewModel.Factory)
                val connectUiState by connectViewModel.uiState.collectAsState()
                LoginScreen(
                    modifier = Modifier.padding(innerPadding),
                    navController,
                    connectUiState = connectUiState,
                    onUsernameChange = connectViewModel::onUsernameChange,
                    onLoginSuccess = {
                        navController.navigate(Screen.Game.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onClickConnect = connectViewModel::connect)
            }
            composable(Screen.Game.route) {
                val gameViewModel: GameViewModel =
                    viewModel(factory = GameViewModel.Factory)
                val gameUiState by gameViewModel.state.collectAsState()
                GameScreen(modifier = Modifier.padding(innerPadding), gameUiState = gameUiState, onColorPressed = gameViewModel::onColorPressed)
            }

        }
    }
}