package com.example.royalcasino.domain.model.card.combination

import com.example.royalcasino.domain.model.card.Card
import com.example.royalcasino.domain.model.card.rank.CardRank

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

    fun canFormCombinationType(card: Card, targetType: CardCombinationType): Boolean {
        val tempCards = this.cards.toMutableList()
        tempCards.add(card)
        return determineType(tempCards) == targetType
    }

    fun removeCard(card: Card) {
        cards.remove(card)
        hasUpdate = true
    }

    fun clear() {
        cards.clear()
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
        val sortedCards = cards.sorted()

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

    fun showCardsInCombination() {
        cards.forEach { card: Card ->
            print("$card ")
        }
        println()
    }

    fun deepCopy(): CardCombination {
        return CardCombination(this.cards.map { it.copy() })
    }
}
