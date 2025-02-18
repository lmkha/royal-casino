package com.example.royalcasino.domain.model.player.iterator

import com.example.royalcasino.domain.model.player.Player

class LinearPlayerIterator(private val players: List<Player>) : PlayerIterator {
    private var index = 0

    override fun hasNext(): Boolean {
        return this.index < this.players.size
    }

    override fun next(): Player {
        if (!hasNext()) throw NoSuchElementException("End of list reached!")

        return this.players[index++]
    }
}
