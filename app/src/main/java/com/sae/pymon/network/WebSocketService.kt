package com.sae.pymon.network

import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

@Serializable
sealed class ServerMessage {
    abstract val type: String
}

@Serializable
data class GameOver(
    override val type: String = "game_over",
) : ServerMessage()

@Serializable
data class Welcome(
    override val type: String = "welcome",
    val player_id: Int,
) : ServerMessage()


@Serializable
data class Eliminated(
    override val type: String = "eliminated",
) : ServerMessage()

@Serializable
data class RoundMessage(
    override val type: String = "next_round",
    val sequence: Int
) : ServerMessage()

@Serializable
data class PlayerEliminated(
    override val type: String = "player_eliminated",
    val player_id: Int,
) : ServerMessage()

@Serializable
data class PlayerConnected(
    override val type: String = "player_connected",
    val player_id: Int,
    val username: String
) : ServerMessage()

@Serializable
data class PlayerDisconnected(
    override val type: String = "player_disconnected",
    val player_id: Int,
) : ServerMessage()

@Serializable
data class PlayerReady(
    override val type: String = "player_ready",
    val player_id: Int,
) : ServerMessage()

@Serializable
data class PlayerFinish(
    override val type: String = "player_finish",
    val player_id: Int,
) : ServerMessage()

@Serializable
data class PlayerScore(
    override val type: String = "player_score",
    val player_id: Int,
    val score: Int
) : ServerMessage()



@Serializable
data class PlayerInputMessage(
    val type: String,
    val input: String
)

class WebSocketService(private val serverUrl: String) {

    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    private val _serverEvents = Channel<ServerMessage>(Channel.BUFFERED)
    val serverEvents = _serverEvents.receiveAsFlow()

    private val _connectionState =
        MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    // Canal pour envoyer les messages reçus vers le ViewModel
    private val _incomingMessages = Channel<String>(Channel.BUFFERED)
    val incomingMessages = _incomingMessages.receiveAsFlow()



    fun parseServerMessage(text: String): ServerMessage? {
        val element = json.parseToJsonElement(text)
        val type = element.jsonObject["type"]?.jsonPrimitive?.content

        return when (type) {
            "game_over" -> json.decodeFromJsonElement<GameOver>(element)
            "next_round" -> json.decodeFromJsonElement<RoundMessage>(element)
            "player_connected" -> json.decodeFromJsonElement<PlayerConnected>(element)
            "player_disconnected" -> json.decodeFromJsonElement<PlayerDisconnected>(element)
            "player_ready" -> json.decodeFromJsonElement<PlayerReady>(element)
            "player_eliminated" -> json.decodeFromJsonElement<PlayerEliminated>(element)
            "player_finish" -> json.decodeFromJsonElement<PlayerFinish>(element)
            "eliminated" -> json.decodeFromJsonElement<Eliminated>(element)
            "player_score" -> json.decodeFromJsonElement<PlayerScore>(element)
            "welcome" -> json.decodeFromJsonElement<Welcome>(element)

            else -> null
        }
    }
    fun connect() {
        Log.d("WebSocket", "Try to connect")
        _connectionState.value = ConnectionState.CONNECTING

        val request = Request.Builder()
            .url(serverUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                Log.d("WebSocket", "Connected to server")
                _connectionState.value = ConnectionState.CONNECTED
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received: $text")
                _incomingMessages.trySend(text)

                val message = parseServerMessage(text)
                if (message != null) {
                    _serverEvents.trySend(message)
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Closed: $code / $reason")
                _connectionState.value = ConnectionState.ERROR
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e("WebSocket", "Error", t)
                _connectionState.value = ConnectionState.ERROR
            }
        })
    }

    fun sendMessage(message: String) {
        Log.d("WebSocket", "Message send: $message")
        webSocket?.send(message)
    }

    fun disconnect() {
        webSocket?.close(1000, "Bye")
    }
}