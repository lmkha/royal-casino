package com.example.royalcasino.domain.model.card.combination

import com.example.royalcasino.domain.model.card.Card
import com.example.royalcasino.domain.model.card.rank.CardRank

class CardCombination(cards: List<Card> = emptyList()) {
    private var cards: MutableList<Card> = cards.toMutableList()
    private var type: CardCombinationType = CardCombinationType.NO_COMBINATION
    private var hasUpdate: Boolean = true

    fun getAllCards(): List<Card> {
        return cards.toList()
    }

    fun getCard(index: Int): Card {
        if (index < 0 || index >= cards.size) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for card list of size ${cards.size}")
        }
        return cards[index]
    }

    fun getSize(): Int {
        return cards.size
    }

    fun addCard(card: Card) {
        cards.add(card)
        hasUpdate = true
    }

    fun removeCard(card: Card) {
        cards.remove(card)
        hasUpdate = true
    }

    fun clear() {
        cards.clear()
    }

    fun getType(): CardCombinationType {
        if (hasUpdate) {
            type = determineType()
            hasUpdate = false
        }
        return this.type
    }

    private fun determineType(): CardCombinationType {
        if (cards.size == 0) return CardCombinationType.NO_COMBINATION

        // Check Single
        if (cards.size == 1) {
            return CardCombinationType.SINGLE
        }

        // Check Pair
        if (cards.size == 2) {
            if (cards[0].rank == cards[1].rank) {
                return CardCombinationType.PAIR
            }
            return CardCombinationType.NO_COMBINATION
        }

        // Check Three Of A Kind
        if (cards.size == 3) {
            if (cards[0].rank == cards[1].rank &&
                cards[0].rank == cards[2].rank
            ) {
                return CardCombinationType.THREE_OF_A_KIND
            }
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
        val cardsRank = cards.map { it.rank }
        if (cardsRank.contains(CardRank.TWO)) return CardCombinationType.NO_COMBINATION

        // Check consecutive pairs
        if (cards.size % 2 == 0 && cards.size >= 6) {
            val cardsRankMap = mutableMapOf<Int, Int>()
            for (card in cards) {
                cardsRankMap[card.rank.ordinal] = cardsRankMap.getOrDefault(card.rank.ordinal, 0) + 1
            }
            val pairs = cardsRankMap.filter { it.value == 2 }.keys.sorted()
            var count = 0
            for (i in 0 until  pairs.size - 1) {
                if (pairs[i + 1] - pairs[i] == 1) {
                    count++
                }
            }
            if (count + 1 == pairs.size) {
                return CardCombinationType.CONSECUTIVE_PAIRS
            }
        }

        // Check straight
        val sortedRanks = cards.map { it.rank.ordinal }.sorted()
        var count = 0
        for (i in 0 until  sortedRanks.size - 1) {
            if (sortedRanks[i + 1] - sortedRanks[i] == 1) {
                count++
            }
        }
        if (count + 1 == sortedRanks.size) return CardCombinationType.STRAIGHT

        // If is not any type of these above, it's NO_COMBINATION
        return CardCombinationType.NO_COMBINATION
    }
}
