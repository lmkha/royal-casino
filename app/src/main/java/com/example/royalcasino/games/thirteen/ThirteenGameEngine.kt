package com.example.royalcasino.games.thirteen

import com.example.royalcasino.core.player.Player
import com.example.royalcasino.games.thirteen.core.ThirteenGame
import com.example.royalcasino.games.thirteen.core.combination.CardCombinationType
import com.example.royalcasino.games.thirteen.core.turn.TurnAction
import com.example.royalcasino.core.card.CardState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThirteenGameEngine @Inject constructor() {
    private lateinit var scope: CoroutineScope

    private var gameJob: Job? = null

    private lateinit var game: ThirteenGame

    private val _state = MutableStateFlow(ThirteenGameState())
    val state: StateFlow<ThirteenGameState> = _state.asStateFlow()

    fun setupNewGame(scope: CoroutineScope, players: List<Player>) {

        gameJob?.cancel()

        this.scope = scope

        game = ThirteenGame(players)

        gameJob = this.scope.launch {
            game.currentRound.collectLatest { currentRound ->
                if (currentRound != null) {
                    // currentTurn
                    launch {
                        currentRound.currentTurn.collectLatest { turn ->
                            _state.update { it.copy(currentTurn = turn) }
                        }
                    }

                    // previousTurn
                    launch {
                        currentRound.previousTurn.collectLatest { turn ->
                            _state.update { it.copy(previousTurn = turn) }
                        }
                    }

                    // handGoingToMakeTurn
                    launch {
                        currentRound.ownerOfHandGoingToMakeTurn.collectLatest { handOwner ->
                            _state.update { oldState ->
                                val cardStates = if (oldState.indexOfHandGoingToMakeTurn == 0) {
                                    oldState.cardStates.map { CardState(it.card, selected = false) }
                                } else oldState.cardStates

                                val indexOfHand = players.indexOf(handOwner)

                                oldState.copy(
                                    indexOfHandGoingToMakeTurn = indexOfHand,
                                    enableSkipTurn = indexOfHand == 0 && currentRound.currentTurn.value != null,
                                    enablePlayTurn = if (indexOfHand == 0) isPlayTurnEnabled() else oldState.enablePlayTurn,
                                    cardStates = cardStates,
                                )
                            }
                        }
                    }

                    // remainingTimeForTurn
                    launch {
                        currentRound.remainingTimeForTurn.collectLatest { time ->
                            _state.update { it.copy(remainingTimeForTurn = time) }
                        }
                    }

                    // numberOfRemainingCards
                    launch {
                        game.numberOfRemainingCards.collectLatest { counts ->
                            _state.update { it.copy(numberOfRemainingCards = counts) }
                        }
                    }

                    // my cards in hand
                    launch {
                        game.myHand.cards.collectLatest { cards ->
                            val newCardStates = cards.map { CardState(card = it, selected = false) }
                            _state.update { it.copy(cardStates = newCardStates) }
                        }
                    }

                    // is game over
                    launch {
                        game.isOver.collectLatest { over ->
                            _state.update { it.copy(isOver = over) }
                        }
                    }
                }
            }
        }
    }

    fun startGame() {
        game.setup()
        game.start()
        _state.update { it.copy(started = true) }
    }

    fun clickCard(index: Int) {
        if (!_state.value.cardStates[index].selected) {
            game.myHand.addCardToCombinationByIndex(index)
        } else {
            game.myHand.removeCardFromCombinationByIndex(index)
        }

        val newCardStates = _state.value.cardStates.toMutableList()
        newCardStates[index] = CardState(newCardStates[index].card, !_state.value.cardStates[index].selected)

        val enablePlayTurn = isPlayTurnEnabled()

        _state.update {
            it.copy(cardStates = newCardStates, enablePlayTurn = enablePlayTurn)
        }
    }

    fun playTurn() {
        if (_state.value.indexOfHandGoingToMakeTurn == 0) {
            val turn = game.myHand.submitTurn(TurnAction.PLAY)
            game.processTurn(turn)
        }
    }

    fun skipTurn() {
        if (_state.value.indexOfHandGoingToMakeTurn == 0) {
            val turn = game.myHand.submitTurn(TurnAction.SKIP)
            game.processTurn(turn)
        }
    }

    fun pause() {
        game.pause()
    }

    fun resume() {
        game.resume()
    }

    private fun isPlayTurnEnabled(): Boolean {
        val currentTurnCombination = game.currentRound.value?.currentTurn?.value?.combination
        val myCombination = game.myHand.cardCombination
        return if (currentTurnCombination != null) {
            myCombination.canDefeat(currentTurnCombination)
        } else {
            myCombination.type != CardCombinationType.NO_COMBINATION
        }
    }
}