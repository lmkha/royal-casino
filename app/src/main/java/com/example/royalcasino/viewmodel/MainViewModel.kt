package com.example.royalcasino.viewmodel

import androidx.lifecycle.ViewModel
import com.example.royalcasino.domain.model.card.Card
import com.example.royalcasino.domain.model.card.combination.CardCombination
import com.example.royalcasino.domain.model.game.Game
import com.example.royalcasino.domain.model.player.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val game: Game = Game(players = listOf(
        Player("Kha"),
        Player("Bot1", false),
        Player("Bot2", false),
        Player("Bot3", false),
    ))
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
}

data class UiState(
    val myCardsInHand: List<Card> = emptyList(),
    val currentCombination: CardCombination? = null,
    val previousCombination: CardCombination? = null,
)
