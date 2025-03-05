package com.example.royalcasino.dev

import com.example.royalcasino.domain.model.game.Game
import com.example.royalcasino.domain.model.player.Player
import com.example.royalcasino.domain.model.turn.TurnAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val game = Game(players = listOf(
        Player("kha"),
        Player(name = "Bot1", isHuman = false),
        Player(name = "Player2"),
        Player(name = "Bot3", isHuman = false),
    ))

    game.setupNewGame()
    game.startNewGame()
    println("Game started!")

    val myHand = game.getHand(0)
    myHand.addCardToCombination(0)
    myHand.addCardToCombination(1)
    myHand.addCardToCombination(4)
    delay(3000L)
    game.currentRound?.processTurn(myHand.pushTurnToRound(TurnAction.PLAY))

    val hand2 = game.getHand(2)
    delay(2000L)
    hand2.addCardToCombination(9)
    hand2.addCardToCombination(10)
    hand2.addCardToCombination(11)
    game.currentRound?.processTurn(hand2.pushTurnToRound(TurnAction.PLAY))

    delay(2000L)
    myHand.addCardToCombination(2)
    myHand.addCardToCombination(3)
    myHand.addCardToCombination(4)
    myHand.addCardToCombination(5)
    game.currentRound?.processTurn(myHand.pushTurnToRound(TurnAction.SKIP))

    delay(1000L)
    hand2.addCardToCombination(8)
    hand2.addCardToCombination(6)
    hand2.addCardToCombination(7)
    game.currentRound?.processTurn(hand2.pushTurnToRound(TurnAction.PLAY))

    delay(2000L)
    game.currentRound?.processTurn(myHand.pushTurnToRound(TurnAction.SKIP))

    while (!game.isOver) {
        delay(500L)
    }

    println("Waiting for the game to finish...")
}
