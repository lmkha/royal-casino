package com.example.royalcasino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.royalcasino.domain.model.card.ICardDrawable
import com.example.royalcasino.domain.model.card.combination.CardCombinationType
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
                    // Collect currentTurn
                    launch {
                        currentRound.currentTurn.collectLatest { turn ->
                            _uiState.update { it.copy(currentTurn = turn) }
                        }
                    }

                    // Collect previousTurn
                    launch {
                        currentRound.previousTurn.collectLatest { turn ->
                            _uiState.update { it.copy(previousTurn = turn) }
                        }
                    }

                    // Collect indexOfHandGoingToMakeTurn
                    launch {
                        currentRound.indexOfHandGoingToMakeTurn.collectLatest { newIndex->
                            _uiState.update { oldState->
                                val cardStates = if (oldState.indexOfHandGoingToMakeTurn == 0) {
                                    oldState.cardStates.map { CardState(card = it.card, selected = false) }
                                } else oldState.cardStates

                                oldState.copy(
                                    indexOfHandGoingToMakeTurn = newIndex,
                                    enableSkipTurn = newIndex == 0 && currentRound.currentTurn.value != null,
                                    enablePlayTurn = if (newIndex == 0) isPlayTurnEnabled() else oldState.enablePlayTurn,
                                    cardStates = cardStates,
                                )
                            }
                        }
                    }

                    // Collect remainingTimeForTurn
                    launch {
                        currentRound.remainingTimeForTurn.collectLatest { time ->
                            _uiState.update { it.copy(remainingTimeForTurn = time) }
                        }
                    }

                    // Collect numberOfRemainingCards
                    launch {
                        currentRound.numberOfRemainingCards.collectLatest { numbersOfRemainingCards->
                            _uiState.update { it.copy( numberOfRemainingCards = numbersOfRemainingCards) }
                        }
                    }

                    // Collect my cards in hand
                    launch {
                        _game.getHand(0).cards.collectLatest { cards ->
                            val newCardStates = cards.map { CardState(card = it, selected = false) }
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
        if (_uiState.value.cardStates[index].selected == false) {
            myHand.addCardToCombinationByIndex(index)
        } else {
            myHand.removeCardFromCombinationByIndex(index)
        }

        val newCardStates = _uiState.value.cardStates.toMutableList()
        newCardStates[index] = CardState(newCardStates[index].card, !newCardStates[index].selected)

        val enablePlayTurn = isPlayTurnEnabled()

        _uiState.update {
            it.copy(
                cardStates = newCardStates,
                enablePlayTurn = enablePlayTurn,
            )
        }
    }

    fun playTurn() {
        if (_uiState.value.indexOfHandGoingToMakeTurn == 0) {
            val myHand = _game.getHand(0)
            val turn = myHand.submitTurn(TurnAction.PLAY)
            _game.currentRound.value?.processTurn(turn)
        }
    }

    fun skipTurn() {
        if (_uiState.value.indexOfHandGoingToMakeTurn == 0) {
            _game.currentRound.value?.processTurn(
                _game.getHand(0).submitTurn(TurnAction.SKIP)
            )
        }
    }

    private fun isPlayTurnEnabled(): Boolean {
        val currentTurnCombination = _game.currentRound.value?.currentTurn?.value?.combination
        val myCombination = _game.getHand(0).cardCombination
        var enablePlayTurn: Boolean = if (currentTurnCombination != null) {
            myCombination.canDefeat(currentTurnCombination)
        } else {
            myCombination.type != CardCombinationType.NO_COMBINATION
        }

        return enablePlayTurn
    }
}

data class UiState(
    val started: Boolean = false,
    val isOver: Boolean = false,
    val indexOfHandGoingToMakeTurn: Int = -1,
    val cardStates: List<CardState> = emptyList(),
    val currentTurn: Turn? = null,
    val previousTurn: Turn? = null,
    val remainingTimeForTurn: Long = 0L,
    val enablePlayTurn: Boolean = false,
    val enableSkipTurn: Boolean = false,
    val numberOfRemainingCards: List<Int> = listOf<Int>(0, 0, 0, 0)
)

data class CardState(
    val card: ICardDrawable,
    val selected: Boolean = false,
)
