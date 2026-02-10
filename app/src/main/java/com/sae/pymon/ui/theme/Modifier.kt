package com.sae.pymon.ui.theme

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.shake(
    trigger: Boolean,
    strength: Float = 16f
): Modifier = composed {

    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (!trigger) return@LaunchedEffect

        repeat(6) {
            offsetX.animateTo(
                targetValue = strength,
                animationSpec = tween(30)
            )
            offsetX.animateTo(
                targetValue = -strength,
                animationSpec = tween(30)
            )
        }
        offsetX.animateTo(0f)
    }

    this.graphicsLayer {
        translationX = offsetX.value
    }
}
