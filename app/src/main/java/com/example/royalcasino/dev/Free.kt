package com.example.royalcasino.dev

import com.example.royalcasino.domain.core.card.Card
import com.example.royalcasino.domain.core.card.combination.CardCombination
import com.example.royalcasino.domain.core.card.rank.CardRank
import com.example.royalcasino.domain.core.card.suit.CardSuit
//
//fun main() {
//    val cards = listOf(
//        Card(CardRank.FOUR, CardSuit.SPADE),
//
//        Card(CardRank.FIVE, CardSuit.SPADE),
//        Card(CardRank.FIVE, CardSuit.DIAMOND),
//
//        Card(CardRank.SIX, CardSuit.SPADE),
//        Card(CardRank.SIX, CardSuit.CLUB),
//        Card(CardRank.SIX, CardSuit.DIAMOND),
//
//        Card(CardRank.SEVEN, CardSuit.DIAMOND),
//
//        Card(CardRank.EIGHT, CardSuit.CLUB),
//
//        Card(CardRank.NINE, CardSuit.SPADE),
//        Card(CardRank.NINE, CardSuit.CLUB),
//        Card(CardRank.NINE, CardSuit.HEART),
//    )
//    val straight = getStraightOrNull(cards, 1, 3)
//    straight?.let {
//        println(it.type)
//    }
//}
//
//fun towerOfHaNoi(n: Int, a: Char, b: Char, c: Char) {
//    if (n == 0) return
//
//    towerOfHaNoi(n-1, a, b, c)
//    println("Move disk $n from rod $a to rod $c")
//    towerOfHaNoi(n-1, b, c, a)
//}
//fun getStraightOrNull(cards: List<Card>, startIndex: Int, straightSize: Int) : CardCombination? {
//    if (startIndex < 0 || startIndex >= cards.size) return null
//    if (straightSize < 3 || straightSize > 11) return null
//    if (startIndex + straightSize > cards.size) return null
//
//    var combination = CardCombination()
//    combination.addCard(cards[startIndex])
//    var prevCard = cards[startIndex]
//    var index = startIndex + 1
//
//    while (index < cards.size && cards[index].rank.ordinal - cards[startIndex].rank.ordinal < straightSize) {
//        if (cards[index].rank == CardRank.TWO) return null
//        when (cards[index].rank.ordinal - prevCard.rank.ordinal) {
//            0 -> {}
//            1 -> {
//                if (cards[index].rank.ordinal - cards[startIndex].rank.ordinal == straightSize - 1) {
//                    if (index + 1 == cards.size || cards[index + 1].rank != cards[index].rank) {
//                        combination.addCard(cards[index])
//                        break
//                    }
//                } else {
//                    combination.addCard(cards[index])
//                    prevCard = cards[index]
//                }
//            }
//            else -> { return null }
//        }
//        index++
//    }
//
//    return if (combination.size == straightSize) combination else null
//}
