package com.example.royalcasino.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.royalcasino.domain.core.card.FaceDownCard
import com.example.royalcasino.viewmodel.CardState

@Composable
fun BotHand(
    modifier: Modifier = Modifier,
    numberOfRemainingCard: Int = 0,
    cardAlignment: Alignment = Alignment.TopCenter,
    cardWidth: Dp = 70.dp
) {
    if (numberOfRemainingCard > 0) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            CardItem(
                state = CardState(FaceDownCard),
                modifier = Modifier
                    .width(cardWidth)
                    .align(cardAlignment)
            )
            Text(
                text = numberOfRemainingCard.toString(),
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}