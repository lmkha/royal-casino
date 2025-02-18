package com.example.royalcasino.domain.model

import com.example.royalcasino.domain.model.card.Card
import com.example.royalcasino.domain.model.card.rank.CardRank
import com.example.royalcasino.domain.model.card.suit.CardSuit
import kotlin.random.Random

class Deck private constructor(private var cards: List<Card>) {
    companion object {
        private val prototypeDeck: Deck = Deck(createInitialDeck())

        fun newDeck(): Deck {
            return Deck(prototypeDeck.cards.toList())
        }

        private fun createInitialDeck(): List<Card> {
            val deck = mutableListOf<Card>()
            for (suite in CardSuit.entries) {
                for (rank in CardRank.entries) {
                    deck.add(Card(rank, suite))
                }
            }
            return deck
        }
    }

    fun shuffle() {
        cards = cards.shuffled(Random)
    }

    fun cut() {
        val cutIndex = Random.nextInt(1, this.cards.size)
        val topHalf = this.cards.subList(0, cutIndex)
        val bottomHalf = this.cards.subList(cutIndex, this.cards.size)
        this.cards = bottomHalf + topHalf
    }

    fun getCards(): List<Card> {
        return this.cards
    }

    fun showCardsInDeck(count: Int = this.cards.size) {
        var index = 0
        for (card in this.cards.take(count)) {
            print("${card.rank.label}${card.suit.icon}")
            index++
            if (index < count) {
                print("  ")
            }
        }
        println()
    }
}
