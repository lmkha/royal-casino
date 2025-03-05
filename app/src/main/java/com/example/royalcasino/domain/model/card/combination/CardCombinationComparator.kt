package com.example.royalcasino.domain.model.card.combination

import com.example.royalcasino.domain.model.card.Card
import com.example.royalcasino.domain.model.card.rank.CardRankComparator
import com.example.royalcasino.domain.model.card.suit.CardSuit

class CardCombinationComparator {
    companion object {
        fun compare(combination1: CardCombination, combination2: CardCombination): Int {
            val combination1Type: CardCombinationType = combination1.type
            val combination2Type: CardCombinationType = combination2.type

            if (combination1Type == CardCombinationType.NO_COMBINATION ||
                combination2Type == CardCombinationType.NO_COMBINATION ||
                combination1Type != combination2Type
            ) throw IllegalArgumentException("Invalid comparison between $combination1Type and $combination2Type")

            return when (combination1Type) {
                CardCombinationType.SINGLE -> SingleCombinationComparator.compare(combination1, combination2)
                CardCombinationType.PAIR -> PairCombinationComparator.compare(combination1, combination2)
                CardCombinationType.THREE_OF_A_KIND -> ThreeOfAKindCombinationComparator.compare(combination1, combination2)
                CardCombinationType.FOUR_OF_A_KIND -> FourOfAKindCombinationComparator.compare(combination1, combination2)
                CardCombinationType.STRAIGHT -> StraightCombinationComparator.compare(combination1, combination2)
                CardCombinationType.CONSECUTIVE_PAIRS -> ConsecutivePairsCombinationComparator.compare(combination1, combination2)
                else -> 0
            }
        }
    }
}

class SingleCombinationComparator  {
    companion object {
        fun compare(a: CardCombination, b: CardCombination): Int {
            return a.getCard(0).compareTo(b.getCard(0))
        }
    }
}

class PairCombinationComparator  {
    companion object {
        fun compare(a: CardCombination, b: CardCombination): Int {
            val rankA = a.getCard(0).rank
            val rankB = b.getCard(0).rank
            if (rankA != rankB) {
                return CardRankComparator.compare(rankA, rankB)
            }
            val isAGreater = a.getAllCards().map { it.suit }.contains(CardSuit.HEART)
            return if (isAGreater) 1 else -1
        }
    }
}

class ThreeOfAKindCombinationComparator  {
    companion object {
        fun compare(a: CardCombination, b: CardCombination): Int {
            return CardRankComparator.compare(a.getCard(0).rank, b.getCard(0).rank)
        }
    }
}

class FourOfAKindCombinationComparator {
    companion object {
        fun compare(a: CardCombination, b: CardCombination): Int {
            return CardRankComparator.compare(a.getCard(0).rank, b.getCard(0).rank)
        }
    }
}

class StraightCombinationComparator {
    companion object {
        fun compare(a: CardCombination, b: CardCombination): Int {
            val sizeA: Int = a.size
            val sizeB: Int = b.size
            if (sizeA == 0 || sizeB == 0 || sizeA != sizeB) return 0
            val sortedCardsA: List<Card> = a.getAllCards().sortedBy { it.rank.ordinal }
            val sortedCardsB: List<Card> = b.getAllCards().sortedBy { it.rank.ordinal }
            return sortedCardsA.last().compareTo(sortedCardsB.last())
        }
    }
}

class ConsecutivePairsCombinationComparator {
    companion object {
        fun compare(a: CardCombination, b: CardCombination): Int {
            val sizeA: Int = a.size
            val sizeB: Int = b.size
            if (sizeA == 0 || sizeB == 0) return 0
            if (sizeA > sizeB) return 1
            if (sizeA < sizeB) return -1
            val lastPairA: List<Card> = a.getAllCards().sortedBy { it.rank.ordinal }.subList(sizeA-2, sizeA)
            val lastPairB: List<Card> = b.getAllCards().sortedBy { it.rank.ordinal }.subList(sizeB-2, sizeB)
            return PairCombinationComparator.compare(
                CardCombination(lastPairA),
                CardCombination(lastPairB)
            )
        }
    }
}
