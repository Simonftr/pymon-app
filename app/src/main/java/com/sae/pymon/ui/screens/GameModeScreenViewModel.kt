package com.sae.pymon.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sae.pymon.PymonApplication
import com.sae.pymon.data.GameRepository
import com.sae.pymon.network.ConnectionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

data class GameModeUiState(
    val isConnecting: Boolean = false,
    val navigateToGame: Boolean = false,
    val error: String? = null,
    val currentUsername: String = ""

)

class GameModeViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameModeUiState())
    val uiState = _uiState.asStateFlow()

    fun onSoloSelected()   = launchGameMode("solo")
    fun onSoloAISelected() = launchGameMode("solo_ai")
    fun onMultiSelected()  = launchGameMode("multiplayer")

    init {
        // Afficher le pseudo actuel dès l'ouverture
        _uiState.update { it.copy(currentUsername = repository.getStoredUsername()) }
    }

    fun onChangeUsername(newUsername: String) {
        repository.saveUsername(newUsername)
        repository.sendName(newUsername)
        _uiState.update { it.copy(currentUsername = newUsername) }
    }

    private fun launchGameMode(mode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true, error = null, navigateToGame = false) }

            // 1. Fermer la connexion existante
            repository.disconnect()
            delay(200) // laisser le temps au socket de se fermer proprement

            // 2. Nouvelle connexion
            repository.connect()

            // 3. Attendre la connexion (timeout 5s)
            val connected = withTimeoutOrNull(5_000) {
                repository.connectionState.first { it == ConnectionState.CONNECTED }
            }

            if (connected != null) {
                // 4. Renvoyer le username, puis le mode de jeu
                repository.sendName(repository.getStoredUsername())
                repository.sendGameMode(mode)
                _uiState.update { it.copy(isConnecting = false, navigateToGame = true) }
            } else {
                _uiState.update { it.copy(isConnecting = false, error = "Connexion au serveur échouée") }
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(navigateToGame = false) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PymonApplication
                GameModeViewModel(app.container.gameRepository)
            }
        }
    }
}