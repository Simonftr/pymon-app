package com.sae.pymon.data

import android.util.Log
import com.sae.pymon.network.ConnectionState
import com.sae.pymon.network.WebSocketService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


interface GameRepository {
    val connectionState: StateFlow<ConnectionState>
    val incomingMessages: Flow<String>

    fun sendPlayerInput(color: String)
    fun connect()
    fun disconnect()
}

class NetworkGameRepository(
    private val wsService: WebSocketService
) : GameRepository {
    override val incomingMessages: Flow<String> = wsService.incomingMessages
    override val connectionState = wsService.connectionState

    override fun connect() = wsService.connect()
    override fun disconnect() = wsService.disconnect()

    override fun sendPlayerInput(color: String) {
        wsService.sendMessage(color)
    }

}