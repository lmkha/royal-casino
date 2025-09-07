package com.example.royalcasino.core.card

import com.example.royalcasino.R

object CardResourceIdHelper {
    private val resIdMap = mapOf(
        "TWO_HEART" to R.drawable.two_heart,
        "TWO_DIAMOND" to R.drawable.two_diamond,
        "TWO_CLUB" to R.drawable.two_club,
        "TWO_SPADE" to R.drawable.two_spade,

        "THREE_HEART" to R.drawable.three_heart,
        "THREE_DIAMOND" to R.drawable.three_diamond,
        "THREE_CLUB" to R.drawable.three_club,
        "THREE_SPADE" to R.drawable.three_spade,

        "FOUR_HEART" to R.drawable.four_heart,
        "FOUR_DIAMOND" to R.drawable.four_diamond,
        "FOUR_CLUB" to R.drawable.four_club,
        "FOUR_SPADE" to R.drawable.four_spade,

        "FIVE_HEART" to R.drawable.five_heart,
        "FIVE_DIAMOND" to R.drawable.five_diamond,
        "FIVE_CLUB" to R.drawable.five_club,
        "FIVE_SPADE" to R.drawable.five_spade,

        "SIX_HEART" to R.drawable.six_heart,
        "SIX_DIAMOND" to R.drawable.six_diamond,
        "SIX_CLUB" to R.drawable.six_club,
        "SIX_SPADE" to R.drawable.six_spade,

        "SEVEN_HEART" to R.drawable.seven_heart,
        "SEVEN_DIAMOND" to R.drawable.seven_diamond,
        "SEVEN_CLUB" to R.drawable.seven_club,
        "SEVEN_SPADE" to R.drawable.seven_spade,

        "EIGHT_HEART" to R.drawable.eight_heart,
        "EIGHT_DIAMOND" to R.drawable.eight_diamond,
        "EIGHT_CLUB" to R.drawable.eight_club,
        "EIGHT_SPADE" to R.drawable.eight_spade,

        "NINE_HEART" to R.drawable.nine_heart,
        "NINE_DIAMOND" to R.drawable.nine_diamond,
        "NINE_CLUB" to R.drawable.nine_club,
        "NINE_SPADE" to R.drawable.nine_spade,

        "TEN_HEART" to R.drawable.ten_heart,
        "TEN_DIAMOND" to R.drawable.ten_diamond,
        "TEN_CLUB" to R.drawable.ten_club,
        "TEN_SPADE" to R.drawable.ten_spade,

        "JACK_HEART" to R.drawable.jack_heart,
        "JACK_DIAMOND" to R.drawable.jack_diamond,
        "JACK_CLUB" to R.drawable.jack_club,
        "JACK_SPADE" to R.drawable.jack_spade,

        "QUEEN_HEART" to R.drawable.queen_heart,
        "QUEEN_DIAMOND" to R.drawable.queen_diamond,
        "QUEEN_CLUB" to R.drawable.queen_club,
        "QUEEN_SPADE" to R.drawable.queen_spade,

        "KING_HEART" to R.drawable.king_heart,
        "KING_DIAMOND" to R.drawable.king_diamond,
        "KING_CLUB" to R.drawable.king_club,
        "KING_SPADE" to R.drawable.king_spade,

        "ACE_HEART" to R.drawable.ace_heart,
        "ACE_DIAMOND" to R.drawable.ace_diamond,
        "ACE_CLUB" to R.drawable.ace_club,
        "ACE_SPADE" to R.drawable.ace_spade,
    )

    fun getCardImageResId(card: Card) : Int {
        val key = "${card.rank.name}_${card.suit.name}"
        return resIdMap[key] ?: R.drawable.default_card
    }
}