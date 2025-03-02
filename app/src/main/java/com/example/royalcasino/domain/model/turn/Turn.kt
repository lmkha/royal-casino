package com.example.royalcasino.domain.model.turn

import com.example.royalcasino.domain.model.card.combination.CardCombination
import com.example.royalcasino.domain.model.player.Player

data class Turn(
    val turnAction: TurnAction = TurnAction.SKIP,
    val owner: Player? = null,
    val combination: CardCombination? = null,
) {
    fun deepCopy(): Turn {
        return Turn(
            owner = this.owner,
            turnAction = this.turnAction,
            combination = this.combination?.deepCopy()
        )
    }
}
