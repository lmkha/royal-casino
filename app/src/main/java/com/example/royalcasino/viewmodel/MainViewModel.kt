package com.example.royalcasino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.royalcasino.domain.model.card.ICardDrawable
import com.example.royalcasino.domain.model.game.Game
import com.example.royalcasino.domain.model.player.Player
import com.example.royalcasino.domain.model.turn.Turn
import com.example.royalcasino.domain.model.turn.TurnAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _game: Game = Game(players = listOf(
        Player("Kha"),
        Player("Bot1", false),
        Player("Bot2", false),
        Player("Bot3", false),
    ))
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _game.currentRound.collectLatest { currentRound ->
                if (currentRound != null) {
                    launch {
                        currentRound.currentTurn.collectLatest { turn ->
                            _uiState.update { it.copy(currentTurn = turn) }
                        }
                    }

                    launch {
                        currentRound.previousTurn.collectLatest { turn ->
                            _uiState.update { it.copy(previousTurn = turn) }
                        }
                    }

                    launch {
                        currentRound.handIsMakingTurn.collectLatest { hand ->
                            _uiState.update { it.copy(isMyTurn = hand?.owner?.name == "Kha") }
                        }
                    }

                    launch {
                        currentRound.remainingTimeForTurn.collectLatest { time ->
                            _uiState.update { it.copy(remainingTimeForTurn = time) }
                        }
                    }

                    launch {
                        _game.getHand(0).cards.collectLatest { cards ->
                            val newCardStates = cards.map { CardState(it, false) }
                            _uiState.update {
                                it.copy(cardStates = newCardStates)
                            }
                        }
                    }
                }
            }
        }
    }

    fun startGame() {
        _game.setupNewGame()
        _game.startNewGame()
        _uiState.update { oldState->
            oldState.copy(started = true)
        }
    }

    fun clickCard(index: Int) {
        val myHand = _game.getHand(0)
        myHand.addCardToCombination(index)
        val newCardStates = _uiState.value.cardStates.toMutableList()
        newCardStates[index] = CardState(newCardStates[index].card, !newCardStates[index].selected)
        _uiState.update { it.copy(cardStates = newCardStates) }
    }

    fun playTurn() {
        if (_uiState.value.isMyTurn) {
            val myHand = _game.getHand(0)
            val turn = myHand.submitTurn(TurnAction.PLAY)
            _game.currentRound.value?.processTurn(turn)
        }
    }

    fun skipTurn() {
        if (_uiState.value.isMyTurn) {
            _game.currentRound.value?.processTurn(
                _game.getHand(0).submitTurn(TurnAction.SKIP)
            )
        }
    }

}

data class UiState(
    val started: Boolean = false,
    val isOver: Boolean = false,
    val isMyTurn: Boolean = false,
    val cardStates: List<CardState> = emptyList(),
    val currentTurn: Turn? = null,
    val previousTurn: Turn? = null,
    val remainingTimeForTurn: Long = 0L,
)

data class CardState(
    val card: ICardDrawable,
    val selected: Boolean = false,
)
