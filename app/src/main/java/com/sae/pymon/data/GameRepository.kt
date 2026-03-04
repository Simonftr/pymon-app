package com.sae.pymon.data

import android.R
import android.util.Log
import com.sae.pymon.network.ApiService
import com.sae.pymon.network.ConnectionState
import com.sae.pymon.network.PlayerInputMessage
import com.sae.pymon.network.ScoreResponse
import com.sae.pymon.network.ServerMessage
import com.sae.pymon.network.WebSocketService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface GameRepository {
    val connectionState: StateFlow<ConnectionState>
    val incomingMessages: Flow<String>
    val serverEvents: Flow<ServerMessage>

    fun sendPlayerInput(message: String, type: String)
    fun sendName(username: String)
    fun connect()
    fun disconnect()
    fun sendGameMode(gameMode: String)
    suspend fun getScores(): List<ScoreResponse>

    // NOUVEAU
    fun saveUsername(username: String)
    fun getStoredUsername(): String
}

class NetworkGameRepository(
    private val wsService: WebSocketService,
    private val apiService: ApiService
) : GameRepository {

    // NOUVEAU
    private var storedUsername: String = ""
    override fun saveUsername(username: String) { storedUsername = username }
    override fun getStoredUsername(): String = storedUsername
    override val serverEvents = wsService.serverEvents

    override val incomingMessages: Flow<String> = wsService.incomingMessages
    override val connectionState = wsService.connectionState

    override fun connect() = wsService.connect()
    override fun disconnect() = wsService.disconnect()

    override fun sendPlayerInput(message: String, type: String) {
        val playerInput = PlayerInputMessage(type = type, message)
        wsService.sendMessage(Json.encodeToString(playerInput))
    }
    override fun sendName(username: String) {
        val playerInput = PlayerInputMessage(type = "username", username)
        wsService.sendMessage(Json.encodeToString(playerInput))
    }
    override fun sendGameMode(gameMode: String) {
        val playerInput = PlayerInputMessage(type = "game_mode", gameMode)
        wsService.sendMessage(Json.encodeToString(playerInput))
    }

    override suspend fun getScores(): List<ScoreResponse> = apiService.getScores()

}