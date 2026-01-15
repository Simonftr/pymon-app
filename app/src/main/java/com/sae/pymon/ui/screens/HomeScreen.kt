package com.sae.pymon.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun HomeScreen(
    modifier: Modifier,
    gameUiState: GameState,
    onColorPressed: (String) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pymon!",
            )
            ButtonPymon(Color.Cyan) {onColorPressed("cyan")}


        }
    }
}

@Composable
fun ButtonPymon(color: Color, onPressed: () -> Unit) {
    Surface(
        onClick = { onPressed() },
        color = color,
        shape = RoundedCornerShape(dimensionResource(R.dimen.surface_color_corner_radius)),
        modifier = Modifier.width(150.dp)
            .height(100.dp)
    ) { }
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
    ButtonPymon(Color.Cyan, {})

}