package com.example.royalcasino.domain.model.round

import com.example.royalcasino.domain.model.card.combination.CardCombination
import com.example.royalcasino.domain.model.card.combination.CardCombinationType
import com.example.royalcasino.domain.model.card.rank.CardRank
import com.example.royalcasino.domain.model.hand.Hand
import com.example.royalcasino.domain.model.turn.Turn
import com.example.royalcasino.domain.model.turn.TurnAction

class Round(
    private val hands: List<Hand>,
    startHand: Hand,
    private val onRoundEnd: (wonHand: Hand) -> Unit = {},
    private val onHandFinished: (hand: Hand) -> Unit = {}
) {
    private var currentTurn: Turn? = null
    private var previousTurn: Turn? = null
    private var followingHandIndexes: MutableList<Int> = hands.indices.toMutableList()
    private var currentIndex: Int = hands.indexOf(startHand)

    private fun isAcceptableCombination(combination: CardCombination?) : Boolean {
        val typeOfCombination = combination?.type
        if (typeOfCombination == null || typeOfCombination == CardCombinationType.NO_COMBINATION) return false
        if (currentTurn == null) return true
        if (currentTurn?.combination == null) throw IllegalStateException("Combination of current turn is null.")
        val currentTurnCombinationType = currentTurn?.combination?.type!!

        when (typeOfCombination) {
            CardCombinationType.SINGLE -> {
                if (currentTurnCombinationType == CardCombinationType.SINGLE &&
                    combination > currentTurn?.combination!!
                ) return true
            }
            CardCombinationType.PAIR -> {
                if (currentTurnCombinationType == CardCombinationType.PAIR &&
                    combination > currentTurn?.combination!!
                ) return true
            }
            CardCombinationType.THREE_OF_A_KIND -> {
                if (currentTurnCombinationType == CardCombinationType.THREE_OF_A_KIND &&
                    combination > currentTurn?.combination!!
                ) return true
            }
            CardCombinationType.FOUR_OF_A_KIND -> {
                if (currentTurnCombinationType == CardCombinationType.SINGLE &&
                    currentTurn?.combination?.getCard(0)?.rank == CardRank.TWO
                ) return true

                if (currentTurnCombinationType == CardCombinationType.PAIR &&
                    currentTurn?.combination?.getCard(0)?.rank == CardRank.TWO
                ) return true

                if (currentTurnCombinationType == CardCombinationType.CONSECUTIVE_PAIRS &&
                    currentTurn?.combination?.size == 6
                ) return true

                if (currentTurnCombinationType == CardCombinationType.FOUR_OF_A_KIND &&
                    combination > currentTurn?.combination!!
                ) return true
            }
            CardCombinationType.STRAIGHT -> {
                if (currentTurnCombinationType == CardCombinationType.STRAIGHT &&
                    currentTurn?.combination?.size == combination.size &&
                    combination > currentTurn?.combination!!
                ) return true
            }
            CardCombinationType.CONSECUTIVE_PAIRS -> {
                // both 3 consecutive pairs and 4 consecutive pairs are able to "cut the pig"
                if (currentTurnCombinationType == CardCombinationType.SINGLE &&
                    currentTurn?.combination?.getCard(0)?.rank == CardRank.TWO
                ) return true
                // 4 consecutive pairs is able to "cut 2 pigs"
                if (currentTurnCombinationType == CardCombinationType.PAIR &&
                    currentTurn?.combination?.getCard(0)?.rank == CardRank.TWO &&
                    combination.size == 8
                ) return true
                // 4 consecutive pairs is able to "cut" FOUR_OF_A_KIND"
                if (currentTurnCombinationType == CardCombinationType.FOUR_OF_A_KIND &&
                    combination.size == 8
                ) return true

                // 4 consecutive pairs > 3 consecutive pairs
                if (currentTurnCombinationType == CardCombinationType.CONSECUTIVE_PAIRS &&
                    combination.size > currentTurn?.combination?.size!!
                ) return true

                // compare 4 vs 4, 3 vs 3
                if (currentTurnCombinationType == CardCombinationType.CONSECUTIVE_PAIRS &&
                    combination.size == currentTurn?.combination?.size &&
                    combination > currentTurn?.combination!!
                ) return true
            }
            else -> { return false }
        }
        return false
    }
    private fun makeDecisionForHand(turn: Turn, accept: Boolean) {
        hands[followingHandIndexes[currentIndex]].makeTurn(turn = turn, roundAccept = accept)
    }
    private fun handlePlayTurn(turn: Turn) {
        if (!isAcceptableCombination(turn.combination)) {
            makeDecisionForHand(turn, false)
            throw IllegalArgumentException("Your combination is not be accepted.")
        }

        previousTurn = currentTurn
        currentTurn = turn.deepCopy()
        makeDecisionForHand(turn, true)

        // If this hand had finished, remove it from following hands list
        if (hands[followingHandIndexes[currentIndex]].numberOfRemainingCards == 0) {
            onHandFinished(hands[followingHandIndexes[currentIndex]])
            followingHandIndexes.removeAt(currentIndex)
            if (currentIndex == followingHandIndexes.size) {
                currentIndex = 0
            }
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
        if (hands.isEmpty()) {
            makeDecisionForHand(turn, false)
            throw NoSuchElementException("No hands available to play a turn.")
        }

        if (currentIndex < 0 || currentIndex >= followingHandIndexes.size) {
            makeDecisionForHand(turn, false)
            throw IndexOutOfBoundsException("Invalid current index.")
        }

        val currentHand = hands[followingHandIndexes[currentIndex]]
        if (turn.owner != currentHand.owner) {
            makeDecisionForHand(turn, false)
            throw  IllegalAccessException("It's not your turn.")
        }

        when(turn.turnAction) {
            TurnAction.PLAY -> handlePlayTurn(turn)
            TurnAction.SKIP -> handleSkipTurn(turn)
        }

        // A hand had just finished and there is no hand follow theirs turn, so the next to hand have right to begin the new round
        if (followingHandIndexes.size == 0) {
            for (i in hands.indices) {
                if (hands[i].owner == currentTurn?.owner) {
                    val indexOfHandBeginNextRound = (i + 1) % hands.size
                    onRoundEnd(hands[indexOfHandBeginNextRound])
                    return
                }
            }
            return
        }
        // This case is when you had not finished and there is no hand still follow this round, so you have right to begin the new round
        if (followingHandIndexes.size == 1 && hands[followingHandIndexes[currentIndex]].owner == currentTurn?.owner) {
            onRoundEnd(hands[followingHandIndexes.first()])
        }
    }
}
