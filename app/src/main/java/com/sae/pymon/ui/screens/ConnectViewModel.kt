package com.sae.pymon.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sae.pymon.PymonApplication
import com.sae.pymon.data.GameRepository
import com.sae.pymon.network.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ConnectUiState(
    val username: String = "",
    val isLoading: Boolean = false,
    val isConnected: Boolean = false,
    val error: String? = null
)

class ConnectViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectUiState())
    val uiState = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value) }
    }

    fun connect() {
        repository.saveUsername(_uiState.value.username)  // NOUVEAU
        repository.connect()
    }

    init {
        viewModelScope.launch {
            repository.connectionState.collect { state ->
                when (state) {
                    ConnectionState.CONNECTING ->
                        _uiState.update { it.copy(isLoading = true, error = null) }

                    ConnectionState.CONNECTED ->{
                        _uiState.update { it.copy(isLoading = false, isConnected = true) }
                        repository.sendName(_uiState.value.username)}


                    ConnectionState.ERROR ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Connexion échouée"
                            )
                        }

                    ConnectionState.DISCONNECTED -> Unit
                }
            }
        }
    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[
                    ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
                ] as PymonApplication

                ConnectViewModel(app.container.gameRepository)
            }
        }
    }
}
