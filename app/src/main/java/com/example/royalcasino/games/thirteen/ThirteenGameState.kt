package com.example.royalcasino.games.thirteen

import com.example.royalcasino.games.GameState
import com.example.royalcasino.games.thirteen.core.turn.Turn
import com.example.royalcasino.core.card.CardState

data class ThirteenGameState(
    val started: Boolean = false,
    val isOver: Boolean = false,
    val indexOfHandGoingToMakeTurn: Int = -1,
    val cardStates: List<CardState> = emptyList(),
    val currentTurn: Turn? = null,
    val previousTurn: Turn? = null,
    val remainingTimeForTurn: Long = 0L,
    val enablePlayTurn: Boolean = false,
    val enableSkipTurn: Boolean = false,
    val numberOfRemainingCards: List<Int> = listOf(0, 0, 0, 0)
) : GameState