package com.sae.pymon.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sae.pymon.ui.screens.HomeScreen


@Composable
fun PymonApp(modifier: Modifier = Modifier) {
    Scaffold() { innerPadding ->
        HomeScreen(modifier = Modifier.padding(innerPadding))
    }
}