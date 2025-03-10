package com.example.royalcasino.domain.bot

import com.example.royalcasino.domain.model.hand.Hand
import com.example.royalcasino.domain.model.turn.Turn

interface Bot {
    fun takeHand(hand: Hand) : Bot
    fun makeTurn(opponentTurn: Turn?): Turn
}