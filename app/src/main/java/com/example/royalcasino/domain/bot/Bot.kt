package com.example.royalcasino.domain.bot

import com.example.royalcasino.domain.model.card.rank.CardRank
import com.example.royalcasino.domain.model.hand.Hand
import com.example.royalcasino.domain.model.turn.Turn

abstract class Bot {
    protected lateinit var hand: Hand
    fun takeHand(hand: Hand) : Bot {
        this.hand = hand
        return this
    }
    fun makeTurn(opponentTurn: Turn?) : Turn {
        if (opponentTurn == null) return startNewRoundWithTurn()
        return followTurn(opponentTurn)
    }
    protected fun calculateStraightValueArray() : IntArray {
        val cardsInHand = hand.getCardsInHand()
        val straightLengthArr = IntArray(cardsInHand.size) { 0 }

        straightLengthArr[straightLengthArr.size - 1] = if (cardsInHand.last().rank != CardRank.TWO) 1 else 0

        for (i in cardsInHand.size - 2 downTo 0) {
            if (cardsInHand[i].rank.ordinal == cardsInHand[i + 1].rank.ordinal - 1 && cardsInHand[i + 1].rank != CardRank.TWO) {
                straightLengthArr[i] = straightLengthArr[i + 1] + 1
            } else if (cardsInHand[i].rank == cardsInHand[i + 1].rank) {
                straightLengthArr[i] = straightLengthArr[i + 1]
            } else {
                straightLengthArr[i] = 1
            }
        }

        return straightLengthArr
    }
    protected fun calculatePairValueArray() : IntArray {
        val cardsInHand = hand.getCardsInHand()
        val pairValueArr = IntArray(cardsInHand.size) { 0 }
        if (cardsInHand[cardsInHand.size - 1].rank != CardRank.TWO &&
            cardsInHand[cardsInHand.size - 1].rank == cardsInHand[cardsInHand.size - 2].rank
        ) {
            pairValueArr[pairValueArr.size - 1] = 1
        }

        for (i in cardsInHand.size - 2 downTo 1) {
            if (cardsInHand[i].rank == cardsInHand[i + 1].rank) {
                pairValueArr[i] = pairValueArr[i + 1]
            } else if (cardsInHand[i].rank == cardsInHand[i - 1].rank) {
                if (cardsInHand[i].rank.ordinal == cardsInHand[i + 1].rank.ordinal - 1) {
                    pairValueArr[i] = pairValueArr[i + 1] + 1
                } else {
                    pairValueArr[i] = 1
                }
            } else {
                pairValueArr[i] = 0
            }
        }

        return pairValueArr
    }
    protected abstract fun followTurn(opponentTurn: Turn): Turn
    protected abstract fun startNewRoundWithTurn() : Turn
}
