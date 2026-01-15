package com.sae.pymon.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sae.pymon.ui.screens.GameViewModel
import com.sae.pymon.ui.screens.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel




@Composable
fun PymonApp(modifier: Modifier = Modifier) {

    Scaffold() { innerPadding ->
        val gameViewModel: GameViewModel =
            viewModel(factory = GameViewModel.Factory)
        val gameUiState by gameViewModel.state.collectAsState()

        HomeScreen(modifier = Modifier.padding(innerPadding), gameUiState = gameUiState, onColorPressed = gameViewModel::onColorPressed)
    }
}