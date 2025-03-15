package com.example.royalcasino.domain.core.turn

import com.example.royalcasino.domain.core.card.combination.CardCombination
import com.example.royalcasino.domain.core.player.Player

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
