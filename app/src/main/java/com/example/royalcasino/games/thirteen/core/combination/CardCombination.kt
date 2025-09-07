package com.example.royalcasino.games.thirteen.core.combination

import com.example.royalcasino.core.card.Card
import com.example.royalcasino.core.card.CardRank
import com.example.royalcasino.core.utils.ComparisonResult
import com.example.royalcasino.games.thirteen.core.comparator.CardCombinationComparator
import com.example.royalcasino.games.thirteen.core.combination.CardCombinationType
import com.example.royalcasino.games.thirteen.core.comparator.ThirteenCardComparator

class CardCombination(cards: List<Card> = emptyList()) : Comparable<CardCombination> {
    private var cards: MutableList<Card> = cards.toMutableList()
    private var hasUpdate: Boolean = true
    private var _type: CardCombinationType = CardCombinationType.NO_COMBINATION
    val type: CardCombinationType
        get() {
            if (hasUpdate) {
                _type = determineType(this.cards)
                hasUpdate = false
            }
            return _type
        }
    val size: Int
        get() = cards.size
    fun getAllCards(): List<Card> {
        return cards.toList()
    }

    fun getCard(index: Int): Card {
        if (index < 0 || index >= cards.size) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for card list of size ${cards.size}")
        }
        return cards[index]
    }

    fun addCard(card: Card) {
        cards.add(card)
        hasUpdate = true
    }

    fun clear() {
        cards.clear()
        hasUpdate = true
    }

    fun removeCard(card: Card) {
        cards.remove(card)
        hasUpdate = true
    }

    private fun determineType(cards: List<Card>): CardCombinationType {
        if (cards.isEmpty()) return CardCombinationType.NO_COMBINATION

        // Check Single
        if (cards.size == 1) return CardCombinationType.SINGLE

        // Check Pair
        if (cards.size == 2) {
            if (cards[0].rank == cards[1].rank) return CardCombinationType.PAIR
            return CardCombinationType.NO_COMBINATION
        }

        // Check Three Of A Kind
        if (cards.size == 3 &&
            cards[0].rank == cards[1].rank &&
            cards[0].rank == cards[2].rank)
        {
            return CardCombinationType.THREE_OF_A_KIND
        }

        // Check Four Of A Kind
        if (cards.size == 4 &&
            cards[0].rank == cards[1].rank &&
            cards[0].rank == cards[2].rank &&
            cards[0].rank == cards[3].rank
        ) {
            return CardCombinationType.FOUR_OF_A_KIND
        }

        // Remaining >= 3 card: possible consecutive, straight. If it contains at least one TWO -> NO_COMBINATION
        if (cards.map { it.rank }.contains(CardRank.TWO)) return CardCombinationType.NO_COMBINATION

        // Use for bolt check consecutive and check straight
        val sortedCards = cards.sortedWith { a, b ->
            ThirteenCardComparator.compareTo(a, b)
        }

        // Check consecutive pairs
        if (cards.size % 2 == 0 && cards.size >= 6) {
            if (sortedCards[0].rank == sortedCards[1].rank) {
                var consecutivePairsCheckResult = true
                for (i in 3..< sortedCards.size step 2) {
                    if (sortedCards[i].rank != sortedCards[i-1].rank ||
                        sortedCards[i].rank.ordinal != sortedCards[i-2].rank.ordinal + 1
                    ) {
                        consecutivePairsCheckResult = false
                        break
                    }
                }
                if (consecutivePairsCheckResult) return CardCombinationType.CONSECUTIVE_PAIRS
            }
        }

        // Check straight
        var straightCheckResult = true
        for (i in 1..<sortedCards.size) {
            if (sortedCards[i].rank.ordinal != sortedCards[i-1].rank.ordinal + 1) {
                straightCheckResult = false
                break
            }
        }
        if (straightCheckResult) return CardCombinationType.STRAIGHT

        // If is not any type above, absolute NO_COMBINATION
        return CardCombinationType.NO_COMBINATION
    }

    override fun compareTo(other: CardCombination): Int {
        return CardCombinationComparator.compare(this, other)
    }

    fun canDefeat(other: CardCombination?) : Boolean {
        val typeOfOther = other?.type
        if (typeOfOther == null || typeOfOther == CardCombinationType.NO_COMBINATION) return false
        if (this.type == CardCombinationType.NO_COMBINATION) return false

        when (this.type) {
            CardCombinationType.SINGLE,
            CardCombinationType.PAIR,
            CardCombinationType.THREE_OF_A_KIND -> { return type == typeOfOther && this > other }
            CardCombinationType.STRAIGHT -> {
                return type == typeOfOther &&
                        size == other.size &&
                        ThirteenCardComparator.compareTo(
                            this.getCard(size-1),
                            other.getCard(size-1)
                        ) == ComparisonResult.GREATER.compareValue
            }
            CardCombinationType.FOUR_OF_A_KIND -> {
                if (typeOfOther == CardCombinationType.SINGLE &&
                    other.getCard(0).rank == CardRank.TWO
                ) return true
                if (typeOfOther == CardCombinationType.PAIR &&
                    other.getCard(0).rank == CardRank.TWO
                ) return true
                if (typeOfOther == CardCombinationType.CONSECUTIVE_PAIRS &&
                    other.size == 6
                ) return true
                if (type == typeOfOther && this > other) return true
           }
            CardCombinationType.CONSECUTIVE_PAIRS -> {
                // both 3 consecutive pairs and 4 consecutive pairs are able to "cut the pig"
                if (typeOfOther == CardCombinationType.SINGLE &&
                    other.getCard(0).rank == CardRank.TWO
                ) return true
                // 4 consecutive pairs is able to "cut 2 pigs"
                if (typeOfOther == CardCombinationType.PAIR &&
                    other.getCard(0).rank == CardRank.TWO &&
                    size == 8
                ) return true
                // 4 consecutive pairs is able to "cut" FOUR_OF_A_KIND"
                if (typeOfOther == CardCombinationType.FOUR_OF_A_KIND &&
                    size == 8
                ) return true
                // Compare between consecutive pairs
                if (typeOfOther == CardCombinationType.CONSECUTIVE_PAIRS &&
                    this > other
                ) return true
            }
            else -> { return false }
        }
        return false
    }

    fun deepCopy(): CardCombination {
        return CardCombination(this.cards.map { it.copy() })
    }

    override fun toString(): String {
        var result = ""
        for (card in cards) {
            result += "${card.rank} ${card.suit} - "
        }
        return result
    }
}
