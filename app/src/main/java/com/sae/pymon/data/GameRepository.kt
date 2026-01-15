package com.sae.pymon.data

import com.sae.pymon.network.ApiService


interface GameRepository {
    suspend fun joinGame(username: String) : Boolean
    suspend fun sendPlayerInput(color: String) : Boolean

}

class NetworkGameRepository(
    private val apiService: ApiService
) : GameRepository {
    override suspend fun joinGame(username: String): Boolean {
        return apiService.joinGame(username)
    }

    override suspend fun sendPlayerInput(color: String): Boolean {
        return apiService.sendPlayerInput(color)
    }
}