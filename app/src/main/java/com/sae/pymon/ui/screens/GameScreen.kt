package com.sae.pymon.ui.screens

import android.view.GestureDetector
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sae.pymon.R
import com.sae.pymon.ui.theme.SimonBlue
import com.sae.pymon.ui.theme.SimonGreen
import com.sae.pymon.ui.theme.SimonRed
import com.sae.pymon.ui.theme.SimonYellow


enum class Colors(
    val uiColor: Color,
    val code: String
) {
    BLUE(SimonBlue, "B"),
    RED(SimonRed, "R"),
    GREEN(SimonGreen, "G"),
    YELLOW(SimonYellow, "Y")
}

@Composable
fun GameScreen(
    modifier: Modifier,
    gameUiState: GameState,
    onColorPressed: (String) -> Unit
) {
    val colors = Colors.values().toList()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 140.dp),
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items=colors) { color ->
                ColorButton(
                    color = color.uiColor,
                    onClick = { onColorPressed(color.code) },
                    modifier = Modifier.fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
        }
    }

}


@Composable
fun ColorButton(
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = MaterialTheme.shapes.large,
        onClick = onClick,
        tonalElevation = 6.dp
    ) {}
}

@Preview(showBackground = true,  widthDp = 360,
    heightDp = 640)
@Composable
fun GameScreenPreview() {
    GameScreen(modifier = Modifier, gameUiState = GameState()) { }
}

@Preview(showBackground = true,  widthDp = 100,
    heightDp = 360)
@Composable
fun GameScreenPreviewD() {
    GameScreen(modifier = Modifier, gameUiState = GameState()) { }
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
fun ButtonPreview() {
    ColorButton( Color.Red, {}, modifier = Modifier.size(50.dp))
}