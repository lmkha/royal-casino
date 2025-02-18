package com.example.royalcasino.domain.model.player

import com.example.royalcasino.domain.model.player.iterator.CircularPlayerIterator
import com.example.royalcasino.domain.model.player.iterator.PlayerIterator

class PlayerCollection {
    private var players: MutableList<Player> = mutableListOf()

    fun getSize(): Int {
        return players.size
    }

    fun getIterator(): PlayerIterator {
        return CircularPlayerIterator(this.players)
    }

    fun setPlayers(players: List<Player>) {
        this.players = players.toMutableList()
    }

    fun addPlayer(player: Player) {
        this.players.add(player)
    }

    fun removePlayer(player: Player) {
        this.players.remove(player)
    }
}
