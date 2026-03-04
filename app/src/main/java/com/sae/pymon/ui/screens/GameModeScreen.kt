package com.sae.pymon.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onSoloClick: () -> Unit,
    onMultiClick: () -> Unit,
    onScoreClick: () -> Unit,
    onSoloAIClick: () -> Unit,

) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Choisir un mode de jeu",
            style = MaterialTheme.typography.headlineMedium
        )

        GameModeCard(
            title = "Solo",
            description = "Joue seul et bats ton meilleur score",
            color = SimonBlue,
            onClick = onSoloClick
        )

        GameModeCard(
            title = "Solo AI",
            description = "Joue seul et bats ton meilleur score mais l'IA adapte le jeu à ton niveau",
            color = SimonGreen,
            onClick = onSoloAIClick
        )

        GameModeCard(
            title = "Multijoueur",
            description = "Affronte les autres joueurs en ligne",
            color = SimonRed,
            onClick = onMultiClick
        )

        GameModeCard(
            title = "Score",
            description = "Voir le score des joueurs",
            color = SimonYellow,
            onClick = onScoreClick
        )
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
        onSoloAIClick = {}
    )
}
