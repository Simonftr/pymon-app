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
data class InfoMessage(
    override val type: String = "info",
    val message: String
) : ServerMessage()

@Serializable
data class SequenceMessage(
    override val type: String = "sequence",
    val count: Int
) : ServerMessage()
@Serializable
data class PlayerInputMessage(
    val type: String,
    val input: List<String>
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
            "info" -> json.decodeFromJsonElement<InfoMessage>(element)
            "sequence" -> json.decodeFromJsonElement<SequenceMessage>(element)
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
        webSocket?.send("{\"type\":\"start_game\"}")
    }

    fun sendMessage(message: MutableList<String>, type: String) {
        Log.d("WebSocket", "Message send: $message")
        val input = PlayerInputMessage(type = type, input = message)
        webSocket?.send(Json.encodeToString(input))
    }

    fun disconnect() {
        webSocket?.close(1000, "Bye")
    }
}