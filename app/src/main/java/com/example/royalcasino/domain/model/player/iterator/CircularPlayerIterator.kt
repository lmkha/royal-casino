package com.example.royalcasino.domain.model.player.iterator

import com.example.royalcasino.domain.model.player.Player

class CircularPlayerIterator (private val players: List<Player>) : PlayerIterator {
    private var index = 0

    override fun next(): Player {
        if (this.players.isEmpty()) throw NoSuchElementException("No players in the game")

        val player = this.players[index]
        index = (index + 1) % this.players.size
        return player
    }

    override fun hasNext(): Boolean {
        return this.players.isEmpty()
    }
}
