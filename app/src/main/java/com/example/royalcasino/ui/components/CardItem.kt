package com.example.royalcasino.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.royalcasino.viewmodel.CardState

@Composable
fun CardItem(state: CardState, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier.clickable { onClick() },
    ) {
        Image(
            painter = painterResource(id = state.card.imageResId),
            contentDescription = "Card Image",
            contentScale = ContentScale.Fit,
        )
    }
}
