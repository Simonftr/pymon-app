package com.sae.pymon.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sae.pymon.network.ScoreResponse


@Composable
fun ScoreScreen(
    scoreUiState: ScoreState,
    modifier: Modifier = Modifier,
    retryAction: () -> Unit
) {
    when (scoreUiState) {
        is ScoreState.Loading -> LoadingScreen(modifier.fillMaxSize())
        is ScoreState.Success -> ScoreList(
            modifier = modifier.fillMaxSize().padding(16.dp),
            scores = scoreUiState.scores
        )
        is ScoreState.Error -> ErrorScreen(
            retryAction = retryAction,
            modifier = modifier.fillMaxSize()
        )
    }
}


@Composable
fun ScoreList(
    modifier: Modifier = Modifier,
    scores: List<ScoreResponse>
) {
    Column(modifier = modifier) {

        Text(
            text = "🏆 Leaderboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(scores) { index, score ->
                ScoreItem(
                    rank = index + 1,
                    score = score
                )
            }
        }
    }
}

@Composable
fun ScoreItem(
    rank: Int,
    score: ScoreResponse
) {
    val backgroundColor = when (rank) {
        1 -> Color(0xFFFFD700) // Or
        2 -> Color(0xFFC0C0C0) // Argent
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "#$rank ${score.nom}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${score.score} pts",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "⚠️ Erreur de chargement",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = retryAction) {
            Text("Réessayer")
        }
    }
}
