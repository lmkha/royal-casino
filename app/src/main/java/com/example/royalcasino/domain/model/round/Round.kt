package com.example.royalcasino.domain.model.round

import android.util.Log
import com.example.royalcasino.domain.bot.Bot
import com.example.royalcasino.domain.model.hand.Hand
import com.example.royalcasino.domain.model.turn.Turn
import com.example.royalcasino.domain.model.turn.TurnAction
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
    private val hands: List<Hand>,
    startHand: Hand,
    private val bot: Bot,
    private val onRoundEnd: (wonHand: Hand) -> Unit = {},
    private val onHandFinished: (hand: Hand) -> Unit = {}
) {
    private var followingHandIndexes: MutableList<Int> = hands.indices.toMutableList()
    private var currentIndex: Int = hands.indexOf(startHand)
    private var timeLimitPerTurn: Long = 15000L
    private var turnJob: Job? = null
    private val _currentTurn: MutableStateFlow<Turn?> = MutableStateFlow(null)
    private val _previousTurn: MutableStateFlow<Turn?> = MutableStateFlow(null)
    private val _remainingTimeForTurn: MutableStateFlow<Long> = MutableStateFlow(timeLimitPerTurn)
    private val _handIsMakingTurn: MutableStateFlow<Hand?> = MutableStateFlow(getCurrentHand())

    val handIsMakingTurn: StateFlow<Hand?> = _handIsMakingTurn.asStateFlow()
    val currentTurn: StateFlow<Turn?> = _currentTurn.asStateFlow()
    val previousTurn: StateFlow<Turn?> = _previousTurn.asStateFlow()
    val remainingTimeForTurn: StateFlow<Long> = _remainingTimeForTurn.asStateFlow()

    init {
        nextTurn()
    }

    private fun getCurrentHand() : Hand {
        return hands[followingHandIndexes[currentIndex]]
    }

    private fun makeDecisionForHand(turn: Turn, accept: Boolean) {
        getCurrentHand().applyTurnDecision(turn = turn, roundAccept = accept)
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
        val currentHand = getCurrentHand()
        if (currentHand.numberOfRemainingCards == 0) {
            onHandFinished(currentHand)
            followingHandIndexes.removeAt(currentIndex)
            if (currentIndex == followingHandIndexes.size) { currentIndex = 0 }
            return
        }

        currentIndex = (currentIndex + 1) % followingHandIndexes.size
    }

    private fun handleSkipTurn(turn: Turn) {
        if (_currentTurn.value == null) {
            makeDecisionForHand(turn, false)
            throw IllegalStateException("The first turn of a round cannot be a SKIP turn.")
        }
        makeDecisionForHand(turn, true)
        followingHandIndexes.removeAt(currentIndex)
        if (currentIndex == followingHandIndexes.size) {
            currentIndex = 0
        }
    }

    fun processTurn(turn: Turn) {
        Log.i("CHECK_VAR", "Before process turn")
        turnJob?.cancel()
        turnJob = null

        if (hands.isEmpty()) {
            makeDecisionForHand(turn, false)
            throw NoSuchElementException("No hands available to play a turn.")
        }

        if (currentIndex < 0 || currentIndex >= followingHandIndexes.size) {
            makeDecisionForHand(turn, false)
            throw IndexOutOfBoundsException("Invalid current index.")
        }

        if (turn.owner != getCurrentHand().owner) {
            makeDecisionForHand(turn, false)
            throw  IllegalAccessException("It's not your turn.")
        }

        when(turn.turnAction) {
            TurnAction.PLAY -> handlePlayTurn(turn)
            TurnAction.SKIP -> handleSkipTurn(turn)
        }

        // A hand had just finished and there is no hand follow theirs turn,
        // so the hand next to they have right to begin the new round.
        if (followingHandIndexes.isEmpty()) {
            for (i in hands.indices) {
                if (hands[i].owner == _currentTurn.value?.owner) {
                    val indexOfHandBeginNextRound = (i + 1) % hands.size
                    onRoundEnd(hands[indexOfHandBeginNextRound])
                    return
                }
            }
            return
        }

        // Only 2 hands in following list and 1 hand had just left(index was update by handleSkip), now only you still in this
        // -> You win and you have right to make the first turn of next round.
        if (getCurrentHand().owner == _currentTurn.value?.owner && followingHandIndexes.size == 1) {
            onRoundEnd(hands[followingHandIndexes.first()])
            return
        }

        _handIsMakingTurn.value = getCurrentHand()

        Log.i("CHECK_VAR", "After process turn")
        nextTurn()
    }

    private fun nextTurn() {
        turnJob?.cancel()
        val currentHand = getCurrentHand()

        turnJob = CoroutineScope(Dispatchers.Default).launch {
            _remainingTimeForTurn.value = timeLimitPerTurn

            val countDownJob = launch {
                while (_remainingTimeForTurn.value > 0) {
                    delay(1000)
                    _remainingTimeForTurn.value -= 1000
                }
            }

            if (currentHand.owner.isHuman) {
                delay(timeLimitPerTurn)
                processTurn(currentHand.submitTurn(TurnAction.SKIP))
            } else {
                val delayTime = Random.nextLong(1000L, 3001L)
                delay(delayTime)
                val botTurn: Turn = bot.takeHand(currentHand).makeTurn(_currentTurn.value)
                processTurn(botTurn)
            }

            countDownJob.cancel()
        }
    }
}
