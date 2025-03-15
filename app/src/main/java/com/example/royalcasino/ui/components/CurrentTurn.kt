package com.example.royalcasino.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.royalcasino.domain.core.turn.Turn
import com.example.royalcasino.viewmodel.CardState

@Composable
fun CurrentTurn(turn: Turn?, modifier: Modifier = Modifier) {
    val cardWidth = 60.dp
    val overlapOffset = (0.6*cardWidth.value).dp

    Box(
        modifier = modifier
            .offset(x = -(30.dp))
    ) {
        turn?.combination?.getAllCards()?.forEachIndexed { index, card ->
            CardItem(
                state = CardState(card),
                modifier = modifier
                    .width(cardWidth)
                    .offset(
                        x = (index * overlapOffset.value).dp,
                    )
                    .zIndex(index.toFloat()),
            )
        }
    }
}
