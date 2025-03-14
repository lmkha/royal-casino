package com.example.royalcasino.domain.bot

import com.example.royalcasino.domain.model.card.combination.CardCombinationType
import com.example.royalcasino.domain.model.card.rank.CardRank
import com.example.royalcasino.domain.model.turn.Turn
import com.example.royalcasino.domain.model.turn.TurnAction

// Bot level1 always decide to play whenever its hand is able to play
open class BotLevel1 : Bot() {
    override fun followTurn(opponentTurn: Turn): Turn {
        val opponentCombination = opponentTurn.combination ?:
            throw IllegalArgumentException("Combination of current's round cannot null.")

        when (opponentCombination.type) {
            CardCombinationType.SINGLE -> {
                val cards = hand.getCardsInHand()
                cards.forEachIndexed { index, card ->
                    if (card > opponentCombination.getCard(0)) {
                        hand.addCardToCombinationByIndex(index)
                        return hand.submitTurn(TurnAction.PLAY)
                    }
                }
            }
            CardCombinationType.PAIR -> {
                val cardsInHand = hand.getCardsInHand()
                for (i in 1 until cardsInHand.size) {
                    if (cardsInHand[i].rank == cardsInHand[i - 1].rank &&
                        cardsInHand[i] > opponentCombination.getCard(1)
                    ) {
                        hand.addCardToCombinationByIndex(i - 1)
                        hand.addCardToCombinationByIndex(i)
                        return hand.submitTurn(TurnAction.PLAY)
                    }
                }
            }
            CardCombinationType.THREE_OF_A_KIND -> {
                val cardsInHand = hand.getCardsInHand()
                for (i in 2 until cardsInHand.size) {
                    if (cardsInHand[i].rank == cardsInHand[i - 1].rank &&
                        cardsInHand[i].rank == cardsInHand[i - 2].rank &&
                        cardsInHand[i] > opponentCombination.getCard(0)
                    ) {
                        hand.addCardToCombinationByIndex(i - 2)
                        hand.addCardToCombinationByIndex(i - 1)
                        hand.addCardToCombinationByIndex(i)
                        return hand.submitTurn(TurnAction.PLAY)
                    }
                }
            }
            CardCombinationType.FOUR_OF_A_KIND -> {
                // You need a greater four of a kind OR x4 consecutive pairs

                // Check for greater four of a kind
                val cardsInHand = hand.getCardsInHand()
                for (i in 3 until cardsInHand.size) {
                    if (cardsInHand[i].rank == cardsInHand[i - 1].rank &&
                        cardsInHand[i].rank == cardsInHand[i - 2].rank &&
                        cardsInHand[i].rank == cardsInHand[i - 3].rank &&
                        cardsInHand[i] > opponentCombination.getCard(0)
                    ) {
                        hand.addCardToCombinationByIndex(i - 3)
                        hand.addCardToCombinationByIndex(i - 2)
                        hand.addCardToCombinationByIndex(i - 1)
                        hand.addCardToCombinationByIndex(i)
                        return hand.submitTurn(TurnAction.PLAY)
                    }
                }

                // Check for x4 consecutive pairs
                for (i in 7 until cardsInHand.size) {
                    if (cardsInHand[i].rank != CardRank.TWO &&
                        cardsInHand[i].rank == cardsInHand[i-1].rank &&

                        cardsInHand[i].rank.ordinal == cardsInHand[i-2].rank.ordinal + 1 &&
                        cardsInHand[i].rank.ordinal == cardsInHand[i-3].rank.ordinal + 1 &&

                        cardsInHand[i].rank.ordinal == cardsInHand[i-4].rank.ordinal + 2 &&
                        cardsInHand[i].rank.ordinal == cardsInHand[i-5].rank.ordinal + 2 &&

                        cardsInHand[i].rank.ordinal == cardsInHand[i-6].rank.ordinal + 3 &&
                        cardsInHand[i].rank.ordinal == cardsInHand[i-7].rank.ordinal + 3
                    ) {
                        for (j in i-7..i) {
                            hand.addCardToCombinationByIndex(j)
                        }
                        return hand.submitTurn(TurnAction.PLAY)
                    }
                }
            }
            CardCombinationType.STRAIGHT -> {
                // 3
                // 4 4 3 3 2 1 1 1 0
                // 6 6 7 7 8 9 9 9 J
                // 7 8 9
                val cardsInHand = hand.getCardsInHand()

                if (cardsInHand.size < opponentCombination.size) return hand.submitTurn(TurnAction.SKIP)

                val straightLengthArr = calculateStraightValueArray()
                val targetLength = opponentCombination.size

                for (i in straightLengthArr.indices) {

                    if (straightLengthArr[i] < targetLength) continue

                    hand.addCardToCombinationByIndex(i)
                    var previousCardRank = cardsInHand[i].rank
                    var k = i + 1

                    while (k < straightLengthArr.size && cardsInHand[k].rank.ordinal < cardsInHand[i].rank.ordinal + targetLength) {
                        if (cardsInHand[k].rank.ordinal == previousCardRank.ordinal + 1) {
                            if (cardsInHand[k].rank.ordinal == cardsInHand[i].rank.ordinal + targetLength - 1) {
                                if (cardsInHand[k] > opponentCombination.getCard(targetLength - 1)) {
                                    hand.addCardToCombinationByIndex(k)
                                    break
                                }
                            } else {
                                hand.addCardToCombinationByIndex(k)
                                previousCardRank = cardsInHand[k].rank
                            }
                        }
                        k++
                    }

                    if (k < cardsInHand.size && cardsInHand[k].rank.ordinal == cardsInHand[i].rank.ordinal + targetLength - 1) {
                        return hand.submitTurn(TurnAction.PLAY)
                    } else {
                        hand.removeAllCardFromCombination()
                    }
                }
            }
            CardCombinationType.CONSECUTIVE_PAIRS -> {
                val cardsInHand = hand.getCardsInHand()
                if (cardsInHand.size < 6) return hand.submitTurn(TurnAction.SKIP)
                val oppCombinationSize = opponentCombination.size

                // If it is x3 consecutive pairs: we need greater x3 consecutive pairs, or x4 consecutive pairs, four of a kind
                if (oppCombinationSize == 6) {
                    println()
                }

                // If it is x4 consecutive pairs, you need greater x4 consecutive pairs
                if (oppCombinationSize == 8) {
                    //3 44 555 66 777 8 Q
                    val pairValueArr = calculatePairValueArray()

                    val beginIndex: Int = pairValueArr.indexOf(4)
                    if (beginIndex == -1) return hand.submitTurn(TurnAction.SKIP)

                    var i = beginIndex + 1
                    while (pairValueArr[i] >= 1) {
                        if (pairValueArr[i] == 1 &&
                            pairValueArr[i - 1] == 1 &&
                            cardsInHand[i] > opponentCombination.getCard(oppCombinationSize - 1)
                        ) {
                            break
                        } else {
                            i++
                        }
                    }
                    if (pairValueArr[i] != 1) return hand.submitTurn(TurnAction.SKIP)
                }
            }
            CardCombinationType.NO_COMBINATION -> {
                throw IllegalArgumentException("Combination of current's round is not a valid combination.")
            }
        }

        return hand.submitTurn(TurnAction.SKIP)
    }
    override fun startNewRoundWithTurn() : Turn {
        hand.addCardToCombinationByIndex(0)
        return hand.submitTurn(TurnAction.PLAY)
    }
}
