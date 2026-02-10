package com.sae.pymon.ui.screens

import android.util.Log
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sae.pymon.PymonApplication
import com.sae.pymon.data.GameRepository
import com.sae.pymon.haptic.HapticManager
import com.sae.pymon.network.Eliminated
import com.sae.pymon.network.GameOver
import com.sae.pymon.network.PlayerConnected
import com.sae.pymon.network.PlayerDisconnected
import com.sae.pymon.network.PlayerEliminated
import com.sae.pymon.network.PlayerFinish
import com.sae.pymon.network.PlayerReady
import com.sae.pymon.network.PlayerScore
import com.sae.pymon.network.RoundMessage
import com.sae.pymon.network.Welcome
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class GameState(
    val score: Int = 0,
    val message: String = "",
    val expectedInputs: Int = 0,
    val receivedInputs: Int = 0,
    val players: List<PlayerUi> = emptyList(),
    val inputsEnabled: Boolean = false,
    val isReady: Boolean = false,
    val isGameOver: Boolean = false,
    val myPlayerId: String = ""
)


class GameViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.serverEvents.collect { event ->
                when (event) {
                    is GameOver -> {
                        _state.update { state ->
                            state.copy(
                                players = state.players.map {
                                    if (it.id == event.winner)
                                        it.copy(status = PlayerStatus.WINNER)
                                    else it
                                },
                                isReady = false,
                                inputsEnabled = false,
                                isGameOver = true,
                            )
                        }
                    }

                    is RoundMessage -> {
                        _state.update { state ->
                            state.copy(
                                expectedInputs = event.sequence,
                                receivedInputs = 0,
                                inputsEnabled = true,
                                isGameOver = false,
                                players = state.players.map {
                                    if (it.status == PlayerStatus.FINISH)
                                        it.copy(status = PlayerStatus.NORMAL)
                                    else it
                                }
                            )
                        }
                    }

                    is PlayerConnected -> {
                        _state.update { state ->
                            state.copy(
                                players = state.players + PlayerUi(
                                    id = event.player_id,
                                    status = PlayerStatus.NORMAL,
                                    username = event.username,
                                    score = 0
                                )
                            )
                        }
                    }

                    is PlayerDisconnected -> {
                        _state.update { state ->
                            state.copy(
                                players = state.players.filterNot {
                                    it.id == event.player_id
                                }
                            )
                        }
                    }

                    is PlayerReady -> {
                        _state.update { state ->
                            state.copy(
                                players = state.players.map {
                                    if (it.id == event.player_id)
                                        it.copy(status = PlayerStatus.READY)
                                    else it
                                },
                            )
                        }
                    }

                    is PlayerEliminated -> {
                        _state.update { state ->
                            state.copy(
                                players = state.players.map {
                                    if (it.id == event.player_id)
                                        it.copy(status = PlayerStatus.ELIMINATED)
                                    else it
                                }
                            )
                        }
                    }

                    is PlayerFinish -> {
                        _state.update { state ->
                            state.copy(
                                players = state.players.map {
                                    if (it.id == event.player_id)
                                        it.copy(status = PlayerStatus.FINISH)
                                    else it
                                }
                            )
                        }
                    }

                    is Eliminated -> {
                        _state.update { state ->
                            state.copy(
                                inputsEnabled = false,
                                isGameOver = true,
                            )
                        }
                    }
                    is PlayerScore -> {
                        _state.update { state ->
                            state.copy(
                                players = state.players.map {
                                    if (it.id == event.player_id)
                                        it.copy(score = event.score)
                                    else it
                                },
                                score = if (event.player_id == state.myPlayerId)
                                    event.score
                                else
                                    state.score
                            )
                        }
                    }

                    is Welcome -> {
                        _state.update { state ->
                            state.copy(myPlayerId = event.player_id)
                        }
                    }
                }
            }
        }
    }

    fun resetGameOver() {
        _state.update {
            it.copy(isGameOver = false)
        }
    }
    fun onColorPressed(color: String) {
        // Coroutine pour envoyer la couleur au serveur
        Log.d("onColorPressed", "pressed")
        val current = _state.value
        if (!current.inputsEnabled) return

        val newCount = current.receivedInputs + 1

        _state.update {
            it.copy(receivedInputs = newCount)
        }

        viewModelScope.launch {
            repository.sendPlayerInput(message = color, type = "player_input")
        }

        if (newCount >= current.expectedInputs) {
            _state.update {
                it.copy(inputsEnabled = false)
            }
        }
    }

    fun onReady() {
        // Coroutine pour envoyer la couleur au serveur
        Log.d("onReady", "pressed")
        viewModelScope.launch {
            repository.sendPlayerInput(type = "game", message = "ready")
        }
        _state.update { it.copy(isReady = true) }
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
