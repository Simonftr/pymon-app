package com.sae.pymon.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sae.pymon.PymonApplication
import com.sae.pymon.data.GameRepository
import com.sae.pymon.network.InfoMessage
import com.sae.pymon.network.SequenceMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class GameState(
    val score: Int = 0,
    val message: String = "",
    val expectedInputs: Int = 0,
    val receivedInputs: Int = 0,
    val infoMessage: String? = null,


)


class GameViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state = _state.asStateFlow()

    private val playerInputs: MutableList<String> = mutableListOf()

    init {
        viewModelScope.launch {
            repository.serverEvents.collect { event ->
                when (event) {
                    is InfoMessage -> {
                        _state.update {
                            it.copy(infoMessage = event.message)
                        }
                    }

                    is SequenceMessage -> {
                        playerInputs.clear()

                        _state.update {
                            it.copy(
                                expectedInputs = event.count,
                            )
                        }
                    }
                }
            }
        }
    }

    fun onColorPressed(color: String) {
        // Coroutine pour envoyer la couleur au serveur
        Log.d("onColorPressed", "pressed")
        playerInputs.add(color)



        if (playerInputs.size == _state.value.expectedInputs) {
            val type = "player_input"
            viewModelScope.launch {
                repository.sendPlayerInput(playerInputs, type)
            }
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
