package com.example.royalcasino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.royalcasino.core.player.Player
import com.example.royalcasino.games.thirteen.ThirteenGameEngine
import com.example.royalcasino.games.thirteen.ThirteenGameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThirteenGameViewModel @Inject constructor(
    private val engine: ThirteenGameEngine
) : ViewModel() {
    private val players = listOf(
        Player("Kha"),
        Player("Bot1", false),
        Player("Bot2", false),
        Player("Bot3", false),
    )

    val gameState: StateFlow<ThirteenGameState> = engine.state

    fun startGame() {
        engine.setupNewGame(viewModelScope, players)
        engine.startGame()
    }

    fun clickCard(index: Int) = engine.clickCard(index)

    fun playTurn() = engine.playTurn()

    fun skipTurn() = engine.skipTurn()
}