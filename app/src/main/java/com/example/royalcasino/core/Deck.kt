package com.example.royalcasino.core

import com.example.royalcasino.core.card.Card
import com.example.royalcasino.core.card.CardRank
import com.example.royalcasino.core.card.CardSuit
import kotlin.random.Random

class Deck private constructor(private var cards: List<Card>) {
    companion object {
        fun newInstance(): Deck {
            val newCards = createCardsForNewDeck()
            return Deck(newCards)
        }

        // It will create new list of cards in every single call
        private fun createCardsForNewDeck(): List<Card> {
            return CardSuit.entries.flatMap { suit ->
                CardRank.entries.map { rank ->
                    Card(rank, suit)
                }
            }
        }
    }

    fun shuffle() {
        cards = cards.shuffled(Random.Default)
    }

    fun cut() {
        val cutIndex = Random.Default.nextInt(1, this.cards.size)
        val topHalf = this.cards.subList(0, cutIndex)
        val bottomHalf = this.cards.subList(cutIndex, this.cards.size)
        this.cards = bottomHalf + topHalf
    }

    fun getCards(): List<Card> {
        return this.cards
    }

    fun getDevDeck01Original(): List<Card> {
        return listOf(
            Card(CardRank.FOUR, CardSuit.DIAMOND),
            Card(CardRank.TWO, CardSuit.HEART),
            Card(CardRank.FOUR, CardSuit.SPADE),
            Card(CardRank.FOUR, CardSuit.HEART),
            Card(CardRank.SIX, CardSuit.SPADE),
            Card(CardRank.THREE, CardSuit.SPADE),
//            Card(CardRank.THREE, CardSuit.HEART),
            Card(CardRank.EIGHT, CardSuit.HEART),
            Card(CardRank.ACE, CardSuit.DIAMOND),
            Card(CardRank.ACE, CardSuit.CLUB),
            Card(CardRank.NINE, CardSuit.CLUB),
            Card(CardRank.JACK, CardSuit.DIAMOND),
            Card(CardRank.FIVE, CardSuit.DIAMOND),
            Card(CardRank.SEVEN, CardSuit.DIAMOND),

            Card(CardRank.TWO, CardSuit.DIAMOND),
            Card(CardRank.JACK, CardSuit.HEART),
            Card(CardRank.TEN, CardSuit.DIAMOND),
            Card(CardRank.QUEEN, CardSuit.HEART),
            Card(CardRank.EIGHT, CardSuit.DIAMOND),
            Card(CardRank.NINE, CardSuit.DIAMOND),
            Card(CardRank.SEVEN, CardSuit.HEART),
            Card(CardRank.JACK, CardSuit.CLUB),
            Card(CardRank.NINE, CardSuit.HEART),
            Card(CardRank.NINE, CardSuit.SPADE),
            Card(CardRank.QUEEN, CardSuit.SPADE),
            Card(CardRank.SIX, CardSuit.DIAMOND),
            Card(CardRank.KING, CardSuit.SPADE),

            Card(CardRank.ACE, CardSuit.SPADE),
            Card(CardRank.EIGHT, CardSuit.CLUB),
            Card(CardRank.THREE, CardSuit.DIAMOND),
            Card(CardRank.SIX, CardSuit.HEART),
            Card(CardRank.THREE, CardSuit.HEART),
//            Card(CardRank.THREE, CardSuit.SPADE),
            Card(CardRank.FIVE, CardSuit.SPADE),
            Card(CardRank.KING, CardSuit.CLUB),
            Card(CardRank.JACK, CardSuit.SPADE),
            Card(CardRank.QUEEN, CardSuit.CLUB),
            Card(CardRank.TWO, CardSuit.CLUB),
            Card(CardRank.TEN, CardSuit.SPADE),
            Card(CardRank.TEN, CardSuit.CLUB),
            Card(CardRank.QUEEN, CardSuit.DIAMOND),

            Card(CardRank.KING, CardSuit.DIAMOND),
            Card(CardRank.SEVEN, CardSuit.CLUB),
            Card(CardRank.FIVE, CardSuit.HEART),
            Card(CardRank.ACE, CardSuit.HEART),
            Card(CardRank.SEVEN, CardSuit.SPADE),
            Card(CardRank.EIGHT, CardSuit.SPADE),
            Card(CardRank.FIVE, CardSuit.CLUB),
            Card(CardRank.TWO, CardSuit.SPADE),
            Card(CardRank.KING, CardSuit.HEART),
            Card(CardRank.SIX, CardSuit.CLUB),
            Card(CardRank.TEN, CardSuit.HEART),
            Card(CardRank.FOUR, CardSuit.CLUB),
            Card(CardRank.THREE, CardSuit.CLUB),
        )
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