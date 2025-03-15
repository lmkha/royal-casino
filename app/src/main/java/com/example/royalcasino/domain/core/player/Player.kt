package com.example.royalcasino.domain.core.player

open class Player(val name: String, val isHuman: Boolean = true) {
    override fun toString(): String {
        return "[${if (isHuman) "Human" else "Bot"} Player]: {name: $name}"
    }
}
