package com.example.royalcasino.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.royalcasino.core.card.CardState

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun Hand(
    cardsInHand: List<CardState>,
    modifier: Modifier = Modifier,
    isMyTurn: Boolean = false,
    enableSkipTurn: Boolean = false,
    enablePlayTurn: Boolean = false,
    onCardClick: (index: Int) -> Unit = {},
    onPlayTurn: () -> Unit = {},
    onSkipTurn: () -> Unit = {},
) {
    val cardWidth = 60.dp
    val overlapOffset = (0.55*cardWidth.value).dp
    val totalWidth = ((cardsInHand.size - 1) * overlapOffset.value).dp + cardWidth
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Column (modifier = modifier.fillMaxWidth()) {

        if (isMyTurn) {
            Row (
                modifier = Modifier
                    .width((screenWidth*0.6).dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier
                        .width(100.dp)
                        .height(45.dp),
                    colors = ButtonColors(
                        contentColor = Color.White,
                        containerColor = Color.Red,
                        disabledContentColor = Color.DarkGray,
                        disabledContainerColor = Color.LightGray,
                    ),
                    onClick = { onSkipTurn() },
                    enabled = enableSkipTurn,
                ) {
                    Text("Bỏ")
                }
                Button(
                    modifier = Modifier
                        .width(100.dp)
                        .height(45.dp),
                    colors = ButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.Green,
                        disabledContentColor = Color.DarkGray,
                        disabledContainerColor = Color.LightGray,
                    ),
                    onClick = { onPlayTurn() },
                    enabled = enablePlayTurn,
                ) {
                    Text("Đánh")
                }
            }
        }

        Box(modifier = Modifier.offset(x=(screenWidth/2 - totalWidth.value/2).dp)) {
            cardsInHand.forEachIndexed { index, cardState ->
                CardItem(
                    state = cardState,
                    modifier = Modifier
                        .width(cardWidth)
                        .offset(
                            x = (index * overlapOffset.value).dp,
                            y = animateDpAsState(
                                targetValue = if (cardState.selected) (-16).dp else 0.dp
                            ).value
                        )
                        .zIndex(index.toFloat()),
                    onClick = { onCardClick(index) }
                )
            }
        }
    }
}
