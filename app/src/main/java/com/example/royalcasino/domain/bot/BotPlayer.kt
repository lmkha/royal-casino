package com.example.royalcasino.domain.bot

import com.example.royalcasino.domain.model.hand.Hand
import com.example.royalcasino.domain.model.turn.Turn
import com.example.royalcasino.domain.model.turn.TurnAction

// Bot level1 always decide to play whenever its hand is able to play
class BotLevel1(private val hand: Hand) {
    fun makeDecision() : Turn {
        return hand.pushTurnToRound(TurnAction.PLAY)
    }

    private fun decideToPlay() : Boolean {
        return Math.random() > 0.5
    }
}
