package com.example.royalcasino.domain.core.card.rank

enum class CardRank(val label: String) : Comparable<CardRank> {
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K"),
    ACE("A"),
    TWO("2"),
}
