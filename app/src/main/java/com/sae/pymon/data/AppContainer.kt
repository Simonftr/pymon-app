package com.sae.pymon.data

import com.sae.pymon.network.WebSocketService


interface AppContainer {
    val gameRepository: GameRepository
}
class DefaultAppContainer : AppContainer {
    private val wsService: WebSocketService by lazy {
        WebSocketService("ws://10.109.150.93:8765")
    }
    override val gameRepository: GameRepository by lazy {
        NetworkGameRepository(
            wsService = wsService
        )
    }
}