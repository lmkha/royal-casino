package com.example.royalcasino.domain.model.card

import com.example.royalcasino.domain.model.card.rank.CardRankComparator
import com.example.royalcasino.domain.model.card.suit.SuitComparator

class CardComparator {
    companion object {
        fun compare(a: Card, b: Card): Int {
            val rankComparisonResult: Int = CardRankComparator.compare(a.rank, b.rank)
            if (rankComparisonResult != 0) return rankComparisonResult
            return SuitComparator.compare(a.suit, b.suit)
        }
    }
}
