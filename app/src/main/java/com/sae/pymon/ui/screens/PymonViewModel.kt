package com.sae.pymon.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sae.pymon.PymonApplication
import com.sae.pymon.data.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class GameState(
    val score: Int = 0,
    val message: String = ""
)


class GameViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state = _state.asStateFlow()

    init {
        repository.connect()
    }

    fun onColorPressed(color: String) {
        // Coroutine pour envoyer la couleur au serveur
        Log.d("onColorPressed", "pressed")
        viewModelScope.launch {
            repository.sendPlayerInput(color)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()

    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[
                    ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
                ] as PymonApplication

                GameViewModel(app.container.gameRepository)
            }
        }
    }
}
