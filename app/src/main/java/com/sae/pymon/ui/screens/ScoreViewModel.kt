package com.sae.pymon.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sae.pymon.PymonApplication
import com.sae.pymon.data.GameRepository
import com.sae.pymon.network.ScoreResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


sealed interface ScoreState {
    data class Success(val scores: List<ScoreResponse>) : ScoreState
    object Error : ScoreState
    object Loading : ScoreState
}


class ScoreViewModel(
    private val repository: GameRepository

) : ViewModel() {


    var scoreUiState: ScoreState by mutableStateOf(ScoreState.Loading)
        private set

    init {
        Log.d("InitScoreVM", "In")
        loadScore()
    }
    fun loadScore() {
        viewModelScope.launch {
            try {
                scoreUiState = ScoreState.Loading
                scoreUiState = try {
                    ScoreState.Success(repository.getScores())
                } catch (e: IOException) {
                    Log.e("Score", "Erreur", e)
                    ScoreState.Error
                } catch (e: HttpException) {
                    Log.e("Score", "Erreur", e)
                    ScoreState.Error
                }
            } catch (e: Exception) {
                Log.e("Score", "Erreur", e)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[
                    ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
                ] as PymonApplication

                ScoreViewModel(app.container.gameRepository)
            }
        }
    }

}