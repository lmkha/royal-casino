package com.example.royalcasino.domain.model.card.suit

class SuitComparator {
    companion object {
        fun compare(a: CardSuit, b: CardSuit): Int {
            return -(a.ordinal.compareTo(b.ordinal))
        }
    }
}
