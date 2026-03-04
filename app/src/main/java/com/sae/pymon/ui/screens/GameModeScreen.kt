package com.sae.pymon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sae.pymon.ui.theme.SimonBlue
import com.sae.pymon.ui.theme.SimonGreen
import com.sae.pymon.ui.theme.SimonRed
import com.sae.pymon.ui.theme.SimonYellow
@Composable
fun GameModeScreen(
    modifier: Modifier = Modifier,
    uiState: GameModeUiState = GameModeUiState(),
    onSoloClick: () -> Unit,
    onMultiClick: () -> Unit,
    onScoreClick: () -> Unit,
    onSoloAIClick: () -> Unit,
    onChangeUsername: (String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var inputUsername by remember { mutableStateOf("") }

    // Dialog changement de pseudo
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Changer de pseudo") },
            text = {
                OutlinedTextField(
                    value = inputUsername,
                    onValueChange = { inputUsername = it },
                    label = { Text("Nouveau pseudo") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputUsername.isNotBlank()) {
                            onChangeUsername(inputUsername)
                            showDialog = false
                            inputUsername = ""
                        }
                    }
                ) { Text("Confirmer") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Annuler") }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(bottom = 72.dp), // espace pour le bouton en bas
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Choisir un mode de jeu", style = MaterialTheme.typography.headlineMedium)

            GameModeCard("Solo", "Joue seul et bats ton meilleur score", SimonBlue, onSoloClick)
            GameModeCard("Solo AI", "L'IA adapte le jeu à ton niveau", SimonGreen, onSoloAIClick)
            GameModeCard("Multijoueur", "Affronte les autres joueurs en ligne", SimonRed, onMultiClick)
            GameModeCard("Score", "Voir le score des joueurs", SimonYellow, onScoreClick)

            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }

        // Bouton pseudo en bas
        TextButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "\uD83D\uDC64 ${uiState.currentUsername}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // Overlay chargement
        if (uiState.isConnecting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
fun GameModeCard(
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = MaterialTheme.shapes.large,
        color = color,
        tonalElevation = 6.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameModeScreenPreview() {
    GameModeScreen(
        onSoloClick = {},
        onMultiClick = {},
        onScoreClick = {},
        onSoloAIClick = {},
        onChangeUsername = {}
    )
}
