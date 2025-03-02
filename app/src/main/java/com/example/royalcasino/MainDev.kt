package com.example.royalcasino

import com.example.royalcasino.domain.model.card.Card
import com.example.royalcasino.domain.model.card.combination.CardCombination
import com.example.royalcasino.domain.model.card.rank.CardRank
import com.example.royalcasino.domain.model.card.suit.CardSuit

fun main() {
    val combination = CardCombination(cards = listOf(
        Card(CardRank.KING, CardSuit.DIAMOND),
        Card(CardRank.QUEEN, CardSuit.CLUB),
        Card(CardRank.ACE, CardSuit.HEART),
    ))
    println(combination.type)
}
