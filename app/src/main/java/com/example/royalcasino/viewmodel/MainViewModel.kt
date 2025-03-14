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
    private val _players = listOf<Player>(
        Player("Kha"),
        Player("Bot1", false),
        Player("Bot2", false),
        Player("Bot3", false),
    )
    private val _game: Game = Game(players = _players)
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

                    // Collect handGoingToMakeTurn
                    launch {
                        currentRound.ownerOfHandGoingToMakeTurn.collectLatest { handOwner->
                            _uiState.update { oldState->
                                val cardStates = if (oldState.indexOfHandGoingToMakeTurn == 0) {
                                    oldState.cardStates.map { CardState(card = it.card, selected = false) }
                                } else oldState.cardStates

                                val indexOfHand = _players.indexOf(handOwner)

                                oldState.copy(
                                    indexOfHandGoingToMakeTurn = indexOfHand,
                                    enableSkipTurn = indexOfHand == 0 && currentRound.currentTurn.value != null,
                                    enablePlayTurn = if (indexOfHand == 0) isPlayTurnEnabled() else oldState.enablePlayTurn,
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
                        _game.numberOfRemainingCards.collectLatest { numbersOfRemainingCards->
                            _uiState.update { it.copy( numberOfRemainingCards = numbersOfRemainingCards) }
                        }
                    }

                    // Collect my cards in hand
                    launch {
                        _game.myHand.cards.collectLatest { cards ->
                            val newCardStates = cards.map { CardState(card = it, selected = false) }
                            _uiState.update {
                                it.copy(cardStates = newCardStates)
                            }
                        }
                    }

                    // Collect is game over
                    launch {
                        _game.isOver.collectLatest { isOver->
                            _uiState.update { it.copy(isOver = isOver) }
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
        if (_uiState.value.cardStates[index].selected == false) {
            _game.myHand.addCardToCombinationByIndex(index)
        } else {
            _game.myHand.removeCardFromCombinationByIndex(index)
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
            val turn = _game.myHand.submitTurn(TurnAction.PLAY)
            _game.currentRound.value?.processTurn(turn)
        }
    }

    fun skipTurn() {
        if (_uiState.value.indexOfHandGoingToMakeTurn == 0) {
            _game.currentRound.value?.processTurn(
                _game.myHand.submitTurn(TurnAction.SKIP)
            )
        }
    }

    private fun isPlayTurnEnabled(): Boolean {
        val currentTurnCombination = _game.currentRound.value?.currentTurn?.value?.combination
        val myCombination = _game.myHand.cardCombination
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
