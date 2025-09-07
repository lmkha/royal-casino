package com.example.royalcasino.games.thirteen.core.comparator

import com.example.royalcasino.core.card.Card
import com.example.royalcasino.core.card.CardComparator

object ThirteenCardComparator : CardComparator {
    override fun compareTo(a: Card, b: Card): Int {
        val rankComparisonResult = a.rank.compareTo(b.rank)
        if (rankComparisonResult != 0) return rankComparisonResult
        return a.suit.compareTo(b.suit)
    }
}