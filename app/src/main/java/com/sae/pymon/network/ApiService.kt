package com.sae.pymon.network

import android.text.format.DateUtils
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path


@Serializable
data class ScoreResponse(
    val id: Int,
    val nom: String,
    val score: Int,
    val date_obtention: String,
)

interface ApiService {
    @GET("scores")
    suspend fun getScores(): List<ScoreResponse>
}
