package com.example.royalcasino.domain.model.round

import com.example.royalcasino.domain.model.card.combination.CardCombination
import com.example.royalcasino.domain.model.card.combination.CardCombinationType
import com.example.royalcasino.domain.model.card.rank.CardRank
import com.example.royalcasino.domain.model.hand.Hand
import com.example.royalcasino.domain.model.turn.Turn
import com.example.royalcasino.domain.model.turn.TurnAction

class Round(
    private val hands: List<Hand>,
    startIndex: Int,
    private val onRoundEnd: (indexOfWonHand: Int) -> Unit = {}
) {
    private var currentTurn: Turn? = null
    private var previousTurn: Turn? = null
    private var followingHandIndexes: MutableList<Int> = hands.indices.toMutableList()
    private var currentIndex: Int = startIndex

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
                    combination > currentTurn?.combination!!
                ) return true
            }
            CardCombinationType.CONSECUTIVE_PAIRS -> {
                if (currentTurnCombinationType == CardCombinationType.SINGLE &&
                    currentTurn?.combination?.getCard(0)?.rank == CardRank.TWO
                ) return true

                if (currentTurnCombinationType == CardCombinationType.PAIR &&
                    currentTurn?.combination?.getCard(0)?.rank == CardRank.TWO &&
                    combination.size == 8
                ) return true

                if (currentTurnCombinationType == CardCombinationType.CONSECUTIVE_PAIRS &&
                    combination.size > currentTurn?.combination?.size!!
                ) return true

                if (currentTurnCombinationType == CardCombinationType.FOUR_OF_A_KIND &&
                    combination.size == 8
                ) return true

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
    private fun handleFireTurn(turn: Turn) {
        val acceptable = isAcceptableCombination(turn.combination)
        if (!acceptable) {
            makeDecisionForHand(turn, false)
            throw IllegalArgumentException("Your combination is not be accepted.")
        }

        previousTurn = currentTurn
        currentTurn = turn.deepCopy()
        makeDecisionForHand(turn, true)
        currentIndex = (currentIndex + 1) % followingHandIndexes.size
    }
    private fun handleSkipTurn(turn: Turn) {
        if (currentTurn == null) {
            makeDecisionForHand(turn, false)
            throw IllegalStateException("The first turn of a round cannot be a SKIP turn.")
        }
        if (followingHandIndexes.size <= 1) {
            makeDecisionForHand(turn, false)
            throw IllegalStateException("Cannot skip turn when there is only one hand left.")
        }
        followingHandIndexes.removeAt(currentIndex)
        if (currentIndex == followingHandIndexes.size) {
            currentIndex = 0
        }
        makeDecisionForHand(turn, true)
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
            TurnAction.FIRE -> handleFireTurn(turn)
            TurnAction.SKIP -> handleSkipTurn(turn)
        }

        if (followingHandIndexes.size == 1) {
            onRoundEnd(followingHandIndexes.first())
        }
    }
}
