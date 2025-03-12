package com.example.royalcasino.domain.model.hand

import android.util.Log
import com.example.royalcasino.domain.model.card.Card
import com.example.royalcasino.domain.model.card.combination.CardCombination
import com.example.royalcasino.domain.model.card.combination.CardCombinationType
import com.example.royalcasino.domain.model.player.Player
import com.example.royalcasino.domain.model.turn.Turn
import com.example.royalcasino.domain.model.turn.TurnAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Hand(val owner: Player) {
    private val _cards: MutableStateFlow<List<Card>> = MutableStateFlow(emptyList())
    val cards: StateFlow<List<Card>> = _cards.asStateFlow()
    private var cardCombination: CardCombination = CardCombination()
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
    fun addCardToCombination(index: Int) {
        if (index < 0 || index >= _cards.value.size) {
            throw IllegalArgumentException("Index out of hand bounds.")
        }
        if (!cardCombination.getAllCards().contains(_cards.value[index])) {
            cardCombination.addCard(_cards.value[index])
        } else {
            cardCombination.removeCard(_cards.value[index])
        }
    }
    fun removeAllCardFromCombination() {
        cardCombination.clear()
    }
    fun applyTurnDecision(turn: Turn, roundAccept: Boolean) {
        Log.i("CHECK_VAR", "Before apply")
        cardCombination.showCardsInCombination()
        if (turn.turnAction == TurnAction.PLAY && roundAccept) {
            _cards.value = _cards.value.filterNot { it in cardCombination.getAllCards() }
        }
        removeAllCardFromCombination()
        Log.i("CHECK_VAR", "After apply")
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
