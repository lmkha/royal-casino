package com.example.royalcasino.games.thirteen.core.turn

import com.example.royalcasino.games.thirteen.core.combination.CardCombination
import com.example.royalcasino.core.player.Player

data class Turn(
    val turnAction: TurnAction,
    val owner: Player,
    val combination: CardCombination?, // SKIP turn doesn't contain Combination
) {
    fun deepCopy(): Turn {
        return Turn(
            owner = this.owner,
            turnAction = this.turnAction,
            combination = this.combination?.deepCopy()
        )
    }

    override fun toString(): String {
        return "{\n" +
                "\tTurn Action: ${this.turnAction}\n" +
                "\tOwner: ${this.owner.name}\n" +
                "\tCombination: ${this.combination.toString()}" +
                "\n}"
    }
}
