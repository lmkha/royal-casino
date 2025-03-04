package com.example.royalcasino

import com.example.royalcasino.domain.model.game.Game
import com.example.royalcasino.domain.model.player.Player
import com.example.royalcasino.domain.model.turn.TurnAction

fun main() {
    val game = Game(players = listOf(
        Player("Kha"),
        Player("player2"),
        Player("player3"),
        Player("player4"),
    ))

    game.devSetupGame()
    game.startNewGame()

    val hand1 = game.getHand(0)
    val hand2 = game.getHand(1)
    val hand3 = game.getHand(2)
    val hand4 = game.getHand(3)

    hand1.addCardToCombination(0)
    game.processTurn(hand1.pushTurnToRound(TurnAction.PLAY))

    hand2.addCardToCombination(0)
    game.processTurn(hand2.pushTurnToRound(TurnAction.PLAY))

    game.processTurn(hand3.pushTurnToRound(TurnAction.SKIP))

    game.processTurn(hand4.pushTurnToRound(TurnAction.SKIP))

    game.processTurn(hand1.pushTurnToRound(TurnAction.SKIP))

    hand3.addCardToCombination(2)
    hand3.addCardToCombination(0)
    hand3.addCardToCombination(1)
    game.processTurn(hand3.pushTurnToRound(TurnAction.PLAY))

    game.processTurn(hand4.pushTurnToRound(TurnAction.SKIP))

    game.processTurn(hand1.pushTurnToRound(TurnAction.SKIP))

    hand4.addCardToCombination(1)
    game.processTurn(hand4.pushTurnToRound(TurnAction.PLAY))

    hand1.addCardToCombination(0)
    game.processTurn(hand1.pushTurnToRound(TurnAction.PLAY))
}
