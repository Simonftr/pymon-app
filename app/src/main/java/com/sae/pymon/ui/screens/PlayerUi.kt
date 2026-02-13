package com.sae.pymon.ui.screens

data class PlayerUi(
    val id: Int,
    val status: PlayerStatus,
    val username: String,
    val score: Int
)

enum class PlayerStatus {
    NORMAL,
    READY,
    FINISH,
    ELIMINATED,
    WINNER
}