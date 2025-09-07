package com.example.royalcasino.games.thirteen.core.comparator

import com.example.royalcasino.core.card.CardRank

object ThirteenCardRankComparator {
    fun compare(a: CardRank, b: CardRank): Int {
        if (a == b) return 0
        if (a == CardRank.TWO) return 1
        if (b == CardRank.TWO) return -1
        return a.ordinal.compareTo(b.ordinal)
    }
}