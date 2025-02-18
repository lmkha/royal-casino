package com.example.royalcasino.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.royalcasino.domain.model.card.Card
import com.example.royalcasino.domain.model.card.rank.CardRank
import com.example.royalcasino.domain.model.card.suit.CardSuit

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Hand(
            modifier = Modifier.align(Alignment.TopCenter)
        )
        Hand(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun CardItem(card: Card, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = card.getImageResID()),
        contentDescription = "Card Image",
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

@Composable
fun Hand(modifier: Modifier = Modifier) {
    val cardList: List<Card> = listOf(
        Card(CardRank.THREE, CardSuit.DIAMOND),
        Card(CardRank.FOUR, CardSuit.SPADE),
        Card(CardRank.FIVE, CardSuit.SPADE),

        Card(CardRank.QUEEN, CardSuit.CLUB),
        Card(CardRank.KING, CardSuit.DIAMOND),
        Card(CardRank.ACE, CardSuit.HEART),

        Card(CardRank.SEVEN, CardSuit.DIAMOND),
        Card(CardRank.SEVEN, CardSuit.CLUB),

        Card(CardRank.TWO, CardSuit.HEART),
        Card(CardRank.TWO, CardSuit.SPADE),

        Card(CardRank.TEN, CardSuit.HEART),
        Card(CardRank.NINE, CardSuit.CLUB),
        Card(CardRank.THREE, CardSuit.SPADE),
    )

    val cardWidth = 100.dp
    val overlapOffset = 30.dp
    val totalWidth = ((cardList.size - 1) * overlapOffset.value).dp + cardWidth

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.offset(x = -totalWidth / 2)
        ) {
            cardList.forEachIndexed { index, card ->
                CardItem(
                    card = card,
                    modifier = Modifier
                        .width(cardWidth)
                        .offset(x = (index * overlapOffset.value).dp)
                        .zIndex(index.toFloat())
                )
            }
        }
    }
}

@Preview(
    name = "Landscape",
    showBackground = true,
    widthDp = 915,
    heightDp = 411,
)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
