package com.sae.pymon.data

import com.sae.pymon.network.ApiService


interface AppContainer {
    val gameRepository: GameRepository
}
class DefaultAppContainer : AppContainer {

    override val gameRepository: GameRepository by lazy {
        NetworkGameRepository(
            apiService = ApiService()
        )
    }
}