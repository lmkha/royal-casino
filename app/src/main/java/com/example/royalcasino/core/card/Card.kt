package com.example.royalcasino.core.card


data class Card(
    val rank: CardRank,
    val suit: CardSuit,
) : ICardDrawable {
    override fun toString(): String = "${rank.label} ${suit.icon}"

    override val imageResId: Int get() = CardResourceIdHelper.getCardImageResId(this)
}