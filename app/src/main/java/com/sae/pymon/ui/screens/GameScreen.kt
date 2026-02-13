package com.sae.pymon.ui.screens

import android.view.Surface
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sae.pymon.haptic.HapticManager
import com.sae.pymon.sound.SoundManager
import com.sae.pymon.ui.theme.SimonBlue
import com.sae.pymon.ui.theme.SimonGreen
import com.sae.pymon.ui.theme.SimonRed
import com.sae.pymon.ui.theme.SimonYellow
import com.sae.pymon.ui.theme.shake


enum class Colors(
    val uiColor: Color,
    val code: String
) {
    BLUE(SimonBlue, "b"),
    RED(SimonRed, "r"),
    GREEN(SimonGreen, "v"),
    YELLOW(SimonYellow, "j")
}

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameUiState: GameState,
    onColorPressed: (String) -> Unit,
    onReady: () -> Unit,
    resetGameOver: () -> Unit,
) {
    val colors = Colors.entries

    LaunchedEffect(gameUiState.isGameOver) {
        if (gameUiState.isGameOver) HapticManager.error()
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // 🔹 Joueurs en haut
        LazyRow(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(gameUiState.players) { player -> PlayerChip(player) }
        }

        // 🔹 Grille centrée
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            colors.chunked(2).forEach { rowColors ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    rowColors.forEach { color ->
                        ColorButton(
                            color = color,
                            enabled = gameUiState.inputsEnabled,
                            onClick = { onColorPressed(color.code) },
                            modifier = Modifier.size(140.dp) // taille carrée fixe
                        )
                    }
                }
            }
        }

        // 🔹 Bouton Prêt en bas
        if (!gameUiState.isReady) {
            Button(
                onClick = onReady,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text("Prêt")
            }
        }

        // 🔹 Popup superposé
        if (gameUiState.isGameOver) {
            ResultPopup(
                gameUiState = gameUiState,
                resetGameOver = resetGameOver,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun ColorButton(
    color: Colors,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 700f)
    )

    val elevation by animateFloatAsState(
        targetValue = if (pressed) 4f else 12f,
        animationSpec = spring(stiffness = 600f)
    )

    val displayColor = when {
        !enabled -> MaterialTheme.colorScheme.surfaceVariant
        pressed -> color.uiColor.copy(alpha = 0.85f)
        else -> color.uiColor
    }

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown()
                        pressed = true
                        HapticManager.tap()
                        SoundManager.play(color.code)
                        val up = waitForUpOrCancellation()
                        pressed = false
                        if (up != null) onClick()
                    }
                }
            },
            shape = MaterialTheme.shapes.large,
            color = displayColor,
            tonalElevation = elevation.dp,
            shadowElevation = elevation.dp
        ) {
            if (pressed) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(Color.White.copy(alpha = 0.08f))
                )
            }
        }
    }
}



@Composable
fun PlayerChip(player: PlayerUi) {
    val color = when (player.status) {
        PlayerStatus.READY -> MaterialTheme.colorScheme.primary
        PlayerStatus.ELIMINATED -> MaterialTheme.colorScheme.error
        PlayerStatus.NORMAL -> MaterialTheme.colorScheme.onSurface
        PlayerStatus.FINISH -> MaterialTheme.colorScheme.tertiary
        PlayerStatus.WINNER -> Color(0xFFFFD700)
    }

    Surface(
        shape = RoundedCornerShape(50),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Text(
            text = "${player.username} • ${player.score}",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun ResultPopup(
    gameUiState: GameState,
    resetGameOver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 16.dp,
            shadowElevation = 16.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                val message = if (gameUiState.isGameOver) "Partie terminée !" else ""
                Text(
                    text = message,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Votre score : ${gameUiState.score}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(24.dp))

                Button(onClick = resetGameOver) {
                    Text("OK")
                }
            }
        }
    }
}

@Preview(showBackground = true,  widthDp = 360,
    heightDp = 640)
@Composable
fun GameScreenPreview() {
    val gameUiState = GameState(
        inputsEnabled = true,
        players = listOf(
            PlayerUi(id = 1, username ="Player1", status = PlayerStatus.NORMAL, score = 0),
            PlayerUi(id = 2, username ="Player2", status = PlayerStatus.READY, score = 2),
            PlayerUi(id = 3, username ="Player3", status = PlayerStatus.ELIMINATED, score = 10),
            PlayerUi(id = 4, username ="Player4", status = PlayerStatus.FINISH, score = 1),
            PlayerUi(id = 5, username ="Player5", status = PlayerStatus.WINNER, score = 100)

        ),
    )
    GameScreen(gameUiState = gameUiState, onColorPressed = {}, onReady = {}, resetGameOver = {})
}

@Preview(showBackground = true,  widthDp = 100,
    heightDp = 360)
@Composable
fun GameScreenPreviewD() {
    GameScreen(gameUiState = GameState(), onColorPressed = {}, onReady = {}, resetGameOver = {})
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Text(
        text = "Pymon!",
    )
}

@Preview(showBackground = true)
@Composable
fun ResultPopUpPreview() {
    val gameUiState = GameState(
        inputsEnabled = true,
        players = listOf(
            PlayerUi(id = 1, username ="Player1", status = PlayerStatus.NORMAL, score = 0),
            PlayerUi(id = 2, username ="Player2", status = PlayerStatus.READY, score = 2),
            PlayerUi(id = 3, username ="Player3", status = PlayerStatus.ELIMINATED, score = 10),
            PlayerUi(id = 4, username ="Player4", status = PlayerStatus.FINISH, score = 1),
            PlayerUi(id = 5, username ="Player5", status = PlayerStatus.WINNER, score = 100)

        ),
        isGameOver = true
    )
    ResultPopup(gameUiState = gameUiState, {})
}


@Preview(showBackground = true)
@Composable
fun ButtonPreview() {
    ColorButton( Colors.RED, true, {}, modifier = Modifier.size(50.dp))
}