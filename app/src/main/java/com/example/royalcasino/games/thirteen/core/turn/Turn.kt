package com.example.royalcasino.games.thirteen.core.turn

import com.example.royalcasino.games.thirteen.core.combination.CardCombination
import com.example.royalcasino.core.player.Player

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
