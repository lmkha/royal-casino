package com.example.royalcasino.games.thirteen.bot

import com.example.royalcasino.core.utils.ComparisonResult
import com.example.royalcasino.games.thirteen.core.combination.CardCombinationType
import com.example.royalcasino.games.thirteen.core.comparator.ThirteenCardComparator
import com.example.royalcasino.games.thirteen.core.turn.Turn
import com.example.royalcasino.games.thirteen.core.turn.TurnAction

// Bot level1 always decide to play whenever its hand is able to play
open class BotLevel1 : Bot() {
    override fun followTurn(opponentTurn: Turn): Turn {
        val opponentCombination = opponentTurn.combination ?:
            throw IllegalArgumentException("Combination of current's round cannot null.")

        val cards = hand.getCardsInHand()

        when (opponentCombination.type) {
            CardCombinationType.SINGLE -> {
                cards.forEachIndexed { index, card ->
                    if (ThirteenCardComparator.compareTo(card, opponentCombination.getCard(0)) == ComparisonResult.GREATER.compareValue) {
                        hand.addCardToCombinationByIndex(index)
                        return hand.submitTurn(TurnAction.PLAY)
                    }
                }
            }
            CardCombinationType.PAIR -> {
                for (i in 1 until cards.size) {
                    if (cards[i].rank == cards[i - 1].rank &&
                        ThirteenCardComparator.compareTo(cards[i], opponentCombination.getCard(1)) == ComparisonResult.GREATER.compareValue
                    ) {
                        hand.addCardToCombinationByIndex(i - 1)
                        hand.addCardToCombinationByIndex(i)
                        return hand.submitTurn(TurnAction.PLAY)
                    }
                }
            }
            CardCombinationType.THREE_OF_A_KIND -> {
                for (i in 2 until cards.size) {
                    if (cards[i].rank == cards[i - 1].rank &&
                        cards[i].rank == cards[i - 2].rank &&
                        ThirteenCardComparator.compareTo(cards[i], opponentCombination.getCard(0)) == ComparisonResult.GREATER.compareValue
                    ) {
                        hand.addCardToCombinationByIndex(i - 2)
                        hand.addCardToCombinationByIndex(i - 1)
                        hand.addCardToCombinationByIndex(i)
                        return hand.submitTurn(TurnAction.PLAY)
                    }
                }
            }
            CardCombinationType.FOUR_OF_A_KIND -> {
//                // You need a greater four of a kind OR x4 consecutive pairs
                val fourOfAKinds = getFourOfAKindsOrNull(cards)
                fourOfAKinds?.let {
                    if (it[0].canDefeat(opponentCombination)) {
                        hand.applyCombination(it[0])
                        return hand.submitTurn(TurnAction.PLAY)
                    }

                    if (it.size == 2 && it[1].canDefeat(opponentCombination)) {
                        hand.applyCombination(it[1])
                        return hand.submitTurn(TurnAction.PLAY)
                    }
                }

                val fourConsecutivePairs = get4ConsecutivePairsCombinationOrNull(cards)
                fourConsecutivePairs?.let {
                    if (it.canDefeat(opponentCombination)) {
                        hand.applyCombination(it)
                        return hand.submitTurn(TurnAction.SKIP)
                    }
                }
            }
            CardCombinationType.STRAIGHT -> {
                val straightSizes = calculateStraightValueArray(cards)
                val targetSize = opponentCombination.size
                for (i in 0..cards.size - targetSize) {
                    if (straightSizes[i] >= targetSize) {
                        val straight = getStraightOrNull(cards, i, targetSize)
                        straight?.let {
                            if (it.canDefeat(opponentCombination)) {
                                hand.applyCombination(it)
                                return hand.submitTurn(TurnAction.PLAY)
                            }
                        }
                    }
                }
            }
            CardCombinationType.CONSECUTIVE_PAIRS -> {
                /*
                If the opponent's combination is three consecutive pairs, we only need a greater one to beat it. However, in this level of BOT we
                will use the strongest combination we have to make it harder for the opponent's remaining hands to beat.
                 */
                val fourConsecutivePairs = get4ConsecutivePairsCombinationOrNull(cards)
                fourConsecutivePairs?.let {
                    if (it.canDefeat(opponentCombination)) {
                        hand.applyCombination(it)
                        return hand.submitTurn(TurnAction.PLAY)
                    }
                }

                if (opponentCombination.size == 6) {
                    val fourOfAKinds = getFourOfAKindsOrNull(cards)
                    fourOfAKinds?.let {
                        hand.applyCombination(it[0])
                        return hand.submitTurn(TurnAction.PLAY)
                    }

                    val threeConsecutivePairs = get3ConsecutivePairsCombinationOrNull(cards)
                    threeConsecutivePairs?.let {
                        if (it.canDefeat(opponentCombination)) {
                            hand.applyCombination(it)
                            return hand.submitTurn(TurnAction.PLAY)
                        }
                    }
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
