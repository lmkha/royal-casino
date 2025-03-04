package com.example.royalcasino.domain.model.hand

import com.example.royalcasino.domain.model.card.Card
import com.example.royalcasino.domain.model.card.combination.CardCombination
import com.example.royalcasino.domain.model.card.combination.CardCombinationType
import com.example.royalcasino.domain.model.player.Player
import com.example.royalcasino.domain.model.turn.Turn
import com.example.royalcasino.domain.model.turn.TurnAction

class Hand(val owner: Player) {
    private var cards: MutableList<Card> = mutableListOf()
    private var cardCombination: CardCombination = CardCombination()
    val numberOfRemainingCards: Int
        get() = cards.size

    fun receiveCards(cards: List<Card>) {
        this.cards.clear()
        this.cards.addAll(cards)
    }
    fun sortCardsInHand() {
        cards.sort()
    }
    fun getCardsInHand(): List<Card> {
        return cards.toList()
    }
    fun addCardToCombination(index: Int) {
        if (index < 0 || index >= cards.size) {
            throw IllegalArgumentException("Index out of hand bounds.")
        }
        cardCombination.addCard(cards[index])
    }
    private fun removeAllCardFromCombination() {
        cardCombination.clear()
    }
    fun showAllCardsInHand() {
        cards.forEach { card->
            print("$card ")
        }
        println()
    }
    fun makeTurn(turn: Turn, roundAccept: Boolean) {
        if (turn.turnAction == TurnAction.PLAY && roundAccept) {
            cards.removeAll(cardCombination.getAllCards())
        }
        removeAllCardFromCombination()
    }
    fun pushTurnToRound(turnAction: TurnAction): Turn {
        if (turnAction == TurnAction.PLAY && cardCombination.type == CardCombinationType.NO_COMBINATION) {
            throw IllegalStateException("This cards combination is not valid")
        }

        return Turn(
            owner = owner,
            turnAction = turnAction,
            combination = if (turnAction == TurnAction.PLAY) cardCombination else null
        )
    }
}
