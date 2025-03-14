package com.example.royalcasino.dev

import com.example.royalcasino.domain.model.game.Game
import com.example.royalcasino.domain.model.player.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

//fun main(): Unit = runBlocking {
//    val game = Game(players = listOf(
//        Player("kha"),
//        Player(name = "Bot1", isHuman = false),
//        Player(name = "Bot2", isHuman = false),
//        Player(name = "Bot3", isHuman = false),
//    ))
//
//    game.setupNewGame()
//    game.startNewGame()
//    println("Game started!")
//
//    val myHand = game.myHand
//    myHand.addCardToCombinationByIndex(0)
//    myHand.addCardToCombinationByIndex(1)
//    myHand.addCardToCombinationByIndex(4)
//    delay(2000L)
//
//    while (!game.isOver) {
//        delay(50L)
//    }
//
//    println("Waiting for the game to finish...")
//}
