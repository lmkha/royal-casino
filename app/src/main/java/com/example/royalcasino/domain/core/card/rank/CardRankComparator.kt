package com.example.royalcasino.domain.core.card.rank

class CardRankComparator {
    companion object {
        fun compare(a: CardRank, b: CardRank): Int {
            if (a == b) return 0
            if (a == CardRank.TWO) return 1
            if (b == CardRank.TWO) return -1
            return a.ordinal.compareTo(b.ordinal)
        }
    }
}
