package com.sae.pymon.network

import android.util.Log
import kotlinx.coroutines.delay

class ApiService {
    suspend fun joinGame(username: String): Boolean {
        delay(1000)
        return username.isNotBlank()
    }

    suspend fun sendPlayerInput(color: String): Boolean {
        Log.d("sendPlayerInput", "input send")
        delay(300)
        return listOf(true, false).random()
    }
}