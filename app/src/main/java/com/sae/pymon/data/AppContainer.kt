package com.sae.pymon.data

import com.sae.pymon.network.ApiService
import com.sae.pymon.network.WebSocketService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


interface AppContainer {
    val gameRepository: GameRepository
}
class DefaultAppContainer : AppContainer {
    private val baseUrl =
        "http://10.109.150.93:5000"

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()


    private val retrofit = Retrofit.Builder()    .client(client)

        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    private val wsService: WebSocketService by lazy {
        WebSocketService("ws://10.109.150.93:8765")
    }
    override val gameRepository: GameRepository by lazy {
        NetworkGameRepository(
            wsService = wsService,
            apiService = retrofitService
        )
    }
}