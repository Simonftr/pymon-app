package com.sae.pymon.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sae.pymon.ui.Screen


@Composable
fun LoginScreen (
    modifier: Modifier,
    navController: NavController,
    connectUiState: ConnectUiState,
    onUsernameChange: (String) -> Unit,
    onClickConnect: ( () -> Unit) -> Unit,
    onLoginSuccess: () -> Unit

) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {

            // 🎮 Titre
            Text(
                text = "Pymon",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Connexion au serveur de jeu",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 👤 Champ pseudo
            OutlinedTextField(
                value = connectUiState.username,
                onValueChange = onUsernameChange,
                label = { Text("Nom du joueur") },
                singleLine = true,
                enabled = !connectUiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🔌 Bouton connexion
            Button(
                onClick = {
                    onClickConnect(onLoginSuccess)
                },
                enabled = connectUiState.username.isNotBlank() && !connectUiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (connectUiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Se connecter")
                }
            }

            // ❌ Message d’erreur
            connectUiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


