package com.example.royalcasino.domain.core.card

import com.example.royalcasino.R
import com.example.royalcasino.domain.core.card.rank.CardRank
import com.example.royalcasino.domain.core.card.suit.CardSuit

data class Card(
    val rank: CardRank,
    val suit: CardSuit,
) : ICardDrawable, Comparable<Card> {
    override fun toString(): String {
        return "${rank.label} ${suit.icon}"
    }

    override val imageResId: Int
        get() {
            val resourceName = "${rank.name}_${suit.name}"
            return when (resourceName) {
                "TWO_HEART" -> R.drawable.two_heart
                "TWO_DIAMOND" -> R.drawable.two_diamond
                "TWO_CLUB" -> R.drawable.two_club
                "TWO_SPADE" -> R.drawable.two_spade

                "THREE_HEART" -> R.drawable.three_heart
                "THREE_DIAMOND" -> R.drawable.three_diamond
                "THREE_CLUB" -> R.drawable.three_club
                "THREE_SPADE" -> R.drawable.three_spade

                "FOUR_HEART" -> R.drawable.four_heart
                "FOUR_DIAMOND" -> R.drawable.four_diamond
                "FOUR_CLUB" -> R.drawable.four_club
                "FOUR_SPADE" -> R.drawable.four_spade

                "FIVE_HEART" -> R.drawable.five_heart
                "FIVE_DIAMOND" -> R.drawable.five_diamond
                "FIVE_CLUB" -> R.drawable.five_club
                "FIVE_SPADE" -> R.drawable.five_spade

                "SIX_HEART" -> R.drawable.six_heart
                "SIX_DIAMOND" -> R.drawable.six_diamond
                "SIX_CLUB" -> R.drawable.six_club
                "SIX_SPADE" -> R.drawable.six_spade

                "SEVEN_HEART" -> R.drawable.seven_heart
                "SEVEN_DIAMOND" -> R.drawable.seven_diamond
                "SEVEN_CLUB" -> R.drawable.seven_club
                "SEVEN_SPADE" -> R.drawable.seven_spade

                "EIGHT_HEART" -> R.drawable.eight_heart
                "EIGHT_DIAMOND" -> R.drawable.eight_diamond
                "EIGHT_CLUB" -> R.drawable.eight_club
                "EIGHT_SPADE" -> R.drawable.eight_spade

                "NINE_HEART" -> R.drawable.nine_heart
                "NINE_DIAMOND" -> R.drawable.nine_diamond
                "NINE_CLUB" -> R.drawable.nine_club
                "NINE_SPADE" -> R.drawable.nine_spade

                "TEN_HEART" -> R.drawable.ten_heart
                "TEN_DIAMOND" -> R.drawable.ten_diamond
                "TEN_CLUB" -> R.drawable.ten_club
                "TEN_SPADE" -> R.drawable.ten_spade

                "JACK_HEART" -> R.drawable.jack_heart
                "JACK_DIAMOND" -> R.drawable.jack_diamond
                "JACK_CLUB" -> R.drawable.jack_club
                "JACK_SPADE" -> R.drawable.jack_spade

                "QUEEN_HEART" -> R.drawable.queen_heart
                "QUEEN_DIAMOND" -> R.drawable.queen_diamond
                "QUEEN_CLUB" -> R.drawable.queen_club
                "QUEEN_SPADE" -> R.drawable.queen_spade

                "KING_HEART" -> R.drawable.king_heart
                "KING_DIAMOND" -> R.drawable.king_diamond
                "KING_CLUB" -> R.drawable.king_club
                "KING_SPADE" -> R.drawable.king_spade

                "ACE_HEART" -> R.drawable.ace_heart
                "ACE_DIAMOND" -> R.drawable.ace_diamond
                "ACE_CLUB" -> R.drawable.ace_club
                "ACE_SPADE" -> R.drawable.ace_spade

                else -> R.drawable.default_card
            }
        }

    override fun compareTo(other: Card): Int {
        val rankComparisonResult = this.rank.compareTo(other.rank)
        if (rankComparisonResult != 0) return rankComparisonResult
        return this.suit.compareTo(other.suit)
    }
}
