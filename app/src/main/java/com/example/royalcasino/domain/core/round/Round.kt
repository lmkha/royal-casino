package com.example.royalcasino.domain.core.round

import com.example.royalcasino.domain.bot.Bot
import com.example.royalcasino.domain.core.hand.Hand
import com.example.royalcasino.domain.core.player.Player
import com.example.royalcasino.domain.core.turn.Turn
import com.example.royalcasino.domain.core.turn.TurnAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class Round(
    private val _hands: List<Hand>,
    startHand: Hand,
    private val _bot: Bot,
    private val onTurnFinished: () -> Unit = {},
    private val onRoundEnd: (wonHand: Hand) -> Unit = {},
    private val onHandFinished: (hand: Hand) -> Unit = {}
) {
    private var _followingHandIndexes: MutableList<Int> = _hands.indices.toMutableList()
    private var _currentIndex: Int = _hands.indexOf(startHand)
    private var _timeLimitPerTurn: Long = 15000L
    private var _turnJob: Job? = null
    private val _currentTurn: MutableStateFlow<Turn?> = MutableStateFlow(null)
    private val _previousTurn: MutableStateFlow<Turn?> = MutableStateFlow(null)
    private val _remainingTimeForTurn: MutableStateFlow<Long> = MutableStateFlow(_timeLimitPerTurn)
    private val _currentHand: Hand get() { return _hands[_followingHandIndexes[_currentIndex]] }
    private val _ownerOfHandGoingToMakeTurn: MutableStateFlow<Player> = MutableStateFlow(_currentHand.owner)

    val currentTurn: StateFlow<Turn?> = _currentTurn.asStateFlow()
    val previousTurn: StateFlow<Turn?> = _previousTurn.asStateFlow()
    val remainingTimeForTurn: StateFlow<Long> = _remainingTimeForTurn.asStateFlow()
    val ownerOfHandGoingToMakeTurn: StateFlow<Player> = _ownerOfHandGoingToMakeTurn.asStateFlow()

    init {
        nextTurn()
    }

    private fun makeDecisionForHand(turn: Turn, accept: Boolean) {
        _currentHand.applyTurnDecision(turn = turn, roundAccept = accept)
    }

    private fun handlePlayTurn(turn: Turn) {
        if (turn.combination == null)
            throw IllegalArgumentException("Combination in Play Turn can not null.")

        if (_currentTurn.value != null && _currentTurn.value?.combination == null)
            throw IllegalStateException("Combination of current turn is null.")

        if (_currentTurn.value != null && !turn.combination.canDefeat(_currentTurn.value?.combination))
            throw IllegalArgumentException("Your combination cannot defeat current combination.")

        _previousTurn.value = _currentTurn.value
        _currentTurn.value = turn.deepCopy()
        makeDecisionForHand(turn, true)

        // If this hand had finished, remove it from following hands list
        if (_currentHand.numberOfRemainingCards == 0) {
            onHandFinished(_currentHand)
            _followingHandIndexes.removeAt(_currentIndex)
            if (_currentIndex == _followingHandIndexes.size) { _currentIndex = 0 }
            return
        }

        _currentIndex = (_currentIndex + 1) % _followingHandIndexes.size
    }

    private fun handleSkipTurn(turn: Turn) {
        if (_currentTurn.value == null) {
            makeDecisionForHand(turn, false)
            throw IllegalStateException("The first turn of a round cannot be a SKIP turn.")
        }
        makeDecisionForHand(turn, true)
        _followingHandIndexes.removeAt(_currentIndex)
        if (_currentIndex == _followingHandIndexes.size) {
            _currentIndex = 0
        }
    }

    fun processTurn(turn: Turn) {
        _turnJob?.cancel()
        _turnJob = null

        if (_hands.isEmpty()) {
            makeDecisionForHand(turn, false)
            throw NoSuchElementException("No hands available to play a turn.")
        }

        if (_currentIndex < 0 || _currentIndex >= _followingHandIndexes.size) {
            makeDecisionForHand(turn, false)
            throw IndexOutOfBoundsException("Invalid current index.")
        }

        if (turn.owner != _currentHand.owner) {
            makeDecisionForHand(turn, false)
            throw  IllegalAccessException("It's not your turn.")
        }

        when(turn.turnAction) {
            TurnAction.PLAY -> handlePlayTurn(turn)
            TurnAction.SKIP -> handleSkipTurn(turn)
        }

        // A hand had just finished and there is no hand follow theirs turn,
        // so the hand next to they have right to begin the new round.
        if (_followingHandIndexes.isEmpty()) {
            for (i in _hands.indices) {
                if (_hands[i].owner == _currentTurn.value?.owner) {
                    val indexOfHandBeginNextRound = (i + 1) % _hands.size
                    onRoundEnd(_hands[indexOfHandBeginNextRound])
                    return
                }
            }
            return
        }

        // Only 2 hands in following list and 1 hand had just left(index was update by handleSkip), now only you still in this
        // -> You win and you have right to make the first turn of next round.
        if (_currentHand.owner == _currentTurn.value?.owner && _followingHandIndexes.size == 1) {
            onRoundEnd(_hands[_followingHandIndexes.first()])
            return
        }

        onTurnFinished()

        nextTurn()
    }

    private fun nextTurn() {
        _turnJob?.cancel()
        updateOwnerOfHandGoingToMakeTurn()

        _turnJob = CoroutineScope(Dispatchers.Default).launch {
            _remainingTimeForTurn.value = _timeLimitPerTurn

            val countDownJob: Job = launch {
                while (_remainingTimeForTurn.value > 0) {
                    delay(1000)
                    _remainingTimeForTurn.value -= 1000
                }
            }

            var turn: Turn
            if (_currentHand.owner.isHuman) {
                delay(_timeLimitPerTurn)
                turn = if (_currentTurn.value == null) {
                    _bot.takeHand(_currentHand).makeTurn(_currentTurn.value)
                } else {
                    _currentHand.submitTurn(TurnAction.SKIP)
                }
            } else {
                val delayTime = Random.nextLong(1000L, 3001L)
                delay(delayTime)
                turn = _bot.takeHand(_currentHand).makeTurn(_currentTurn.value)
            }
            processTurn(turn)

            countDownJob.cancel()
        }
    }

    private fun updateOwnerOfHandGoingToMakeTurn() {
        _ownerOfHandGoingToMakeTurn.value = _currentHand.owner
    }
}
