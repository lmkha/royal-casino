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
    private lateinit var deck: Deck
    private var hands: MutableList<Hand> = mutableListOf()
    private var currentRound: Round? = null
    private var handWonPreviousRound: Hand
    private var result: MutableList<Player> = mutableListOf()

    init {
        players.forEach { player ->
            hands.add(Hand(player))
        }
        handWonPreviousRound = hands[0]
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

    fun devSetupGame() {
        hands[0].receiveCards(listOf(
            Card(CardRank.SIX, CardSuit.CLUB),
            Card(CardRank.TWO, CardSuit.SPADE),
        ))
        hands[1].receiveCards(listOf(
            Card(CardRank.ACE, CardSuit.DIAMOND),
        ))
        hands[2].receiveCards(listOf(
            Card(CardRank.SIX, CardSuit.DIAMOND),
            Card(CardRank.FIVE, CardSuit.HEART),
            Card(CardRank.SEVEN, CardSuit.CLUB),
        ))
        hands[3].receiveCards(listOf(
            Card(CardRank.NINE, CardSuit.HEART),
            Card(CardRank.EIGHT, CardSuit.SPADE),
        ))
        hands.forEach { it.sortCardsInHand() }
    }

    fun startNewGame() {
        startNewRound()
    }

    private fun startNewRound() {
        currentRound = Round(
            hands = hands.filter { it.numberOfRemainingCards > 0 },
            startHand = handWonPreviousRound,
            onRoundEnd = { wonHand ->
                handWonPreviousRound = wonHand
                startNewRound()
            },
            onHandFinished = { handFinished ->
                handleHandFinished(handFinished)
            }
        )
    }

    fun getHand(handIndex: Int): Hand {
        return hands[handIndex]
    }

    fun processTurn(turn: Turn) {
        currentRound?.processTurn(turn)
    }

    private fun handleHandFinished(hand: Hand) {
        result.add(hand.owner)
        // Game over, add the remaining hand to to end of result list
        if (result.size == hands.size - 1) {
            handleGameOver()
        }
    }

    private fun handleGameOver() {
        val remainingHandOwner = hands.find { it.numberOfRemainingCards != 0 }?.owner
        if (remainingHandOwner != null) {
            result.add(remainingHandOwner)
        }
        showResult()
    }

    private fun showResult() {
        println("Game over!!!\nResult:")
        result.forEachIndexed { index, player ->
            println("Rank ${index+1}: ${player.name}")
        }
    }

    fun checkAutoWin() {
    }
}

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
