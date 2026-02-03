package com.sae.pymon.data

import android.R
import android.util.Log
import com.sae.pymon.network.ConnectionState
import com.sae.pymon.network.ServerMessage
import com.sae.pymon.network.WebSocketService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


interface GameRepository {
    val connectionState: StateFlow<ConnectionState>
    val incomingMessages: Flow<String>
    val serverEvents: Flow<ServerMessage>


    fun sendPlayerInput(colors: MutableList<String>, type: String)
    fun connect()
    fun disconnect()
}

class NetworkGameRepository(
    private val wsService: WebSocketService
) : GameRepository {
    override val serverEvents = wsService.serverEvents

    override val incomingMessages: Flow<String> = wsService.incomingMessages
    override val connectionState = wsService.connectionState

    override fun connect() = wsService.connect()
    override fun disconnect() = wsService.disconnect()

    override fun sendPlayerInput(colors: MutableList<String>, type: String) {
        wsService.sendMessage(colors, type)
    }

}