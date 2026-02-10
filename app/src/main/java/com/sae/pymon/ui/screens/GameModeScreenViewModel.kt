package com.sae.pymon.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sae.pymon.PymonApplication
import com.sae.pymon.data.GameRepository

class GameModeViewModel(
    private val repository: GameRepository
) : ViewModel() {

    fun onSoloSelected() {
        repository.sendGameMode("solo")
    }

    fun onMultiSelected() {
        repository.sendGameMode("multiplayer")
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[
                    ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
                ] as PymonApplication

                GameModeViewModel(app.container.gameRepository)
            }
        }
    }
}


