package com.example.royalcasino.domain.core.hand

import com.example.royalcasino.domain.core.card.Card
import com.example.royalcasino.domain.core.card.combination.CardCombination
import com.example.royalcasino.domain.core.card.combination.CardCombinationType
import com.example.royalcasino.domain.core.player.Player
import com.example.royalcasino.domain.core.turn.Turn
import com.example.royalcasino.domain.core.turn.TurnAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Hand(val owner: Player) {
    private val _cards: MutableStateFlow<List<Card>> = MutableStateFlow(emptyList())
    var cardCombination: CardCombination = CardCombination()
        private set
    val cards: StateFlow<List<Card>> = _cards.asStateFlow()
    val numberOfRemainingCards: Int
        get() = _cards.value.size

    fun receiveCards(cards: List<Card>) {
        this._cards.value = cards
    }
    fun sortCardsInHand() {
        _cards.value = _cards.value.sorted()
    }
    fun getCardsInHand(): List<Card> {
        return _cards.value.toList()
    }
    fun addCardToCombinationByIndex(index: Int) {
        _cards.value.getOrNull(index)?.let { card ->
            cardCombination.addCard(card)
        }
    }
    fun applyCombination(combination: CardCombination) {
        val cardsInCombination = combination.getAllCards()
        for (card in cardsInCombination) {
            if (!_cards.value.contains(card)) {
                throw IllegalArgumentException("Invalid combination: Some cards do not exist in this hand.")
            }
            cardCombination.addCard(card)
        }
    }
    fun removeCardFromCombinationByIndex(index: Int) {
        _cards.value.getOrNull(index)?.let { card ->
            cardCombination.removeCard(card)
        }
    }
    fun removeAllCardFromCombination() {
        cardCombination.clear()
    }
    fun applyTurnDecision(turn: Turn, roundAccept: Boolean) {
        if (turn.turnAction == TurnAction.PLAY && roundAccept) {
            _cards.value = _cards.value.filterNot { it in cardCombination.getAllCards() }
        }
        removeAllCardFromCombination()
    }
    fun submitTurn(turnAction: TurnAction): Turn {
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
