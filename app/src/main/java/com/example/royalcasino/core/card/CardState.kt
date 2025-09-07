package com.example.royalcasino.core.card

data class CardState(
    val card: ICardDrawable,
    val selected: Boolean = false,
)