package com.example.royalcasino.games.thirteen.core.comparator

import com.example.royalcasino.core.card.Card
import com.example.royalcasino.core.card.CardSuit
import com.example.royalcasino.games.thirteen.core.combination.CardCombination
import com.example.royalcasino.games.thirteen.core.combination.CardCombinationType

object CardCombinationComparator {
    fun compare(combination1: CardCombination, combination2: CardCombination): Int {
        val combination1Type: CardCombinationType = combination1.type
        val combination2Type: CardCombinationType = combination2.type

        if (combination1Type == CardCombinationType.NO_COMBINATION ||
            combination2Type == CardCombinationType.NO_COMBINATION ||
            combination1Type != combination2Type
        ) throw IllegalArgumentException("Invalid comparison between $combination1Type and $combination2Type")

        return when (combination1Type) {
            CardCombinationType.SINGLE -> compareSingle(combination1, combination2)
            CardCombinationType.PAIR -> comparePair(combination1, combination2)
            CardCombinationType.THREE_OF_A_KIND -> compareThreeOfAKind(combination1, combination2)
            CardCombinationType.FOUR_OF_A_KIND -> compareFourOfAKind(combination1, combination2)
            CardCombinationType.STRAIGHT -> compareStraight(combination1, combination2)
            CardCombinationType.CONSECUTIVE_PAIRS -> compareConsecutivePairs(combination1, combination2)
            else -> 0
        }
    }

    private fun compareSingle(a: CardCombination, b: CardCombination) : Int {
        return ThirteenCardComparator.compareTo(a.getCard(0), b.getCard(0))
    }

    private fun comparePair(a: CardCombination, b: CardCombination) : Int {
        val rankA = a.getCard(0).rank
        val rankB = b.getCard(0).rank
        if (rankA != rankB) {
            ThirteenCardRankComparator.compare(rankA, rankB)
        }
        val isAGreater = a.getAllCards().map { it.suit }.contains(CardSuit.HEART)
        return if (isAGreater) 1 else -1
    }

    private fun compareThreeOfAKind(a: CardCombination, b: CardCombination) : Int {
        return ThirteenCardRankComparator.compare(a.getCard(0).rank, b.getCard(0).rank)
    }

    private fun compareFourOfAKind(a: CardCombination, b: CardCombination) : Int {
        return ThirteenCardRankComparator.compare(a.getCard(0).rank, b.getCard(0).rank)
    }

    private fun compareStraight(a: CardCombination, b: CardCombination) : Int {
        val sizeA: Int = a.size
        val sizeB: Int = b.size
        if (sizeA == 0 || sizeB == 0 || sizeA != sizeB) return 0
        val sortedCardsA: List<Card> = a.getAllCards().sortedBy { it.rank.ordinal }
        val sortedCardsB: List<Card> = b.getAllCards().sortedBy { it.rank.ordinal }
        return ThirteenCardComparator.compareTo(sortedCardsA.last(), sortedCardsB.last())
    }

    private fun compareConsecutivePairs(a: CardCombination, b: CardCombination) : Int {
        val sizeA: Int = a.size
        val sizeB: Int = b.size
        if (sizeA == 0 || sizeB == 0) return 0
        if (sizeA > sizeB) return 1
        if (sizeA < sizeB) return -1
        val lastPairA: List<Card> = a.getAllCards().sortedBy { it.rank.ordinal }.subList(sizeA-2, sizeA)
        val lastPairB: List<Card> = b.getAllCards().sortedBy { it.rank.ordinal }.subList(sizeB-2, sizeB)
        return comparePair(
            CardCombination(lastPairA),
            CardCombination(lastPairB)
        )
    }
}