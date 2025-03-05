package com.example.royalcasino.domain.model.round

import com.example.royalcasino.domain.model.hand.Hand
import com.example.royalcasino.domain.model.turn.Turn
import com.example.royalcasino.domain.model.turn.TurnAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Round(
    private val hands: List<Hand>,
    startHand: Hand,
    private val onRoundEnd: (wonHand: Hand) -> Unit = {},
    private val onHandFinished: (hand: Hand) -> Unit = {}
) {
    private var followingHandIndexes: MutableList<Int> = hands.indices.toMutableList()
    private var currentTurn: Turn? = null
    private var previousTurn: Turn? = null
    private var currentIndex: Int = hands.indexOf(startHand)
    private var timeLimitPerTurn: Long = 5000L
    private var turnJob: Job? = null

    init {
        nextTurn()
    }

    private fun getCurrentHand() : Hand {
        return hands[followingHandIndexes[currentIndex]]
    }

    private fun makeDecisionForHand(turn: Turn, accept: Boolean) {
        hands[followingHandIndexes[currentIndex]].makeTurn(turn = turn, roundAccept = accept)
    }

    private fun handlePlayTurn(turn: Turn) {
        if (turn.combination == null)
            throw IllegalArgumentException("Combination in Play Turn can not null.")

        if (currentTurn != null && currentTurn?.combination == null)
            throw IllegalStateException("Combination of current turn is null.")

        if (currentTurn != null && !turn.combination.canDefeat(currentTurn?.combination))
            throw IllegalArgumentException("Your combination cannot defeat current combination.")

        previousTurn = currentTurn
        currentTurn = turn.deepCopy()
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
        if (currentTurn == null) {
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
        println("Processing turn of ${turn.owner}")
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
                if (hands[i].owner == currentTurn?.owner) {
                    val indexOfHandBeginNextRound = (i + 1) % hands.size
                    onRoundEnd(hands[indexOfHandBeginNextRound])
                    return
                }
            }
            return
        }

        // Only 2 hands in following list and 1 hand had just left(index was update by handleSkip), now only you still in this
        // -> You win and you have right to make the first turn of next round.
        if (getCurrentHand().owner == currentTurn?.owner && followingHandIndexes.size == 1) {
            onRoundEnd(hands[followingHandIndexes.first()])
            return
        }

        nextTurn()
    }

    private fun nextTurn() {
        turnJob?.cancel()
        val currentHand = getCurrentHand()

        turnJob = CoroutineScope(Dispatchers.Default).launch {
            if (currentHand.owner.isHuman) {
                delay(timeLimitPerTurn)
                processTurn(currentHand.pushTurnToRound(TurnAction.SKIP))
            } else {
                delay(1000L)
//                val botTurn = currentHand.getBOTurn
//                processTurn(botTurn)
                processTurn(currentHand.pushTurnToRound(TurnAction.SKIP))
            }
        }
    }
}
