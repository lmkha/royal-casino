package com.example.royalcasino.domain.model.game

import com.example.royalcasino.domain.model.Deck
import com.example.royalcasino.domain.model.card.Card
import com.example.royalcasino.domain.model.card.rank.CardRank
import com.example.royalcasino.domain.model.card.suit.CardSuit
import com.example.royalcasino.domain.model.hand.Hand
import com.example.royalcasino.domain.model.player.Player
import com.example.royalcasino.domain.model.round.Round
import com.example.royalcasino.domain.model.turn.Turn
import com.example.royalcasino.domain.model.turn.TurnAction

// Game: A game round
class Game(players: List<Player>) {
    private var hands: MutableList<Hand> = mutableListOf()
    private lateinit var deck: Deck
    private var currentRound: Round? = null
    var indexOfHandWonPreviousRound = 0
        private set
    private var result: MutableList<Player> = mutableListOf()

    init {
        players.forEach { player ->
            hands.add(Hand(player))
        }
    }

    fun setupNewGame() {
        if (hands.size < 2 || hands.size > 4) throw Exception("Number of players must be between 2 and 4")
        deck = Deck.newDeck().apply { shuffle() }

        val cardsOfDeck = deck.getDevDeck01Original().toMutableList()
        hands.forEach { hand->
            cardsOfDeck.take(13).let {drawnCards ->
                hand.receiveCards(drawnCards)
                hand.sortCardsInHand()
                cardsOfDeck.removeAll(drawnCards)
            }
        }
    }

    fun startNewGame() {
        currentRound = Round(hands = hands, startIndex = 0, onRoundEnd = { indexOfWonHand ->
            indexOfHandWonPreviousRound = indexOfWonHand
            startNewRound()
        })
    }
    private fun startNewRound() {
        currentRound = Round(hands = hands, startIndex = indexOfHandWonPreviousRound) { indexOfWonHand ->
            indexOfHandWonPreviousRound = indexOfWonHand
        }
    }

    fun getMyHand(): Hand {
        return hands[0]
    }

    fun getHand(handIndex: Int): Hand {
        return hands[handIndex]
    }

    fun processTurn(turn: Turn) {
        currentRound?.processTurn(turn)
    }
    fun isGameOver() {

    }

    fun checkAutoWin() {

    }
}

fun main() {
    val game = Game(players = listOf(
        Player("player1"),
        Player("player2"),
        Player("player3"),
        Player("player4"),
    ))
    game.setupNewGame()
    val hand01: Hand = game.getMyHand()
    val hand02: Hand = game.getHand(1)
    val hand03: Hand = game.getHand(2)
    val hand04: Hand = game.getHand(3)

    game.startNewGame()

    hand01.addCardToCombination(0)
    hand01.addCardToCombination(1)
    hand01.addCardToCombination(4)

    println("Index of previous round won: ${game.indexOfHandWonPreviousRound}")

    game.processTurn(hand01.pushTurnToRound(TurnAction.FIRE))
    println("Hand01 cards:")
    hand01.showAllCardsInHand()

    hand02.addCardToCombination(2)
    hand02.addCardToCombination(4)
    hand02.addCardToCombination(6)

    game.processTurn(hand02.pushTurnToRound(TurnAction.FIRE))

    game.processTurn(hand03.pushTurnToRound(TurnAction.SKIP))

    game.processTurn(hand04.pushTurnToRound(TurnAction.SKIP))

    hand01.addCardToCombination(2)
    hand01.addCardToCombination(3)
    hand01.addCardToCombination(4)

    game.processTurn(hand01.pushTurnToRound(TurnAction.SKIP))

    hand02.addCardToCombination(8)
    game.processTurn(hand02.pushTurnToRound(TurnAction.FIRE))
    hand02.showAllCardsInHand()
}
