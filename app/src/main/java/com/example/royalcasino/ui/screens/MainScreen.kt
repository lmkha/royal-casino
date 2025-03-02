package com.example.royalcasino.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.royalcasino.domain.model.card.Card
import com.example.royalcasino.domain.model.card.FaceDownCard
import com.example.royalcasino.domain.model.card.ICardDrawable
import com.example.royalcasino.domain.model.card.rank.CardRank
import com.example.royalcasino.domain.model.card.suit.CardSuit

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        CardItem(
            state = CardItemState(FaceDownCard),
            modifier = Modifier
                .padding(top = 24.dp)
                .width(70.dp)
                .align(Alignment.TopCenter)
        )

        CardItem(
            state = CardItemState(FaceDownCard),
            modifier = Modifier
                .padding(start = 56.dp)
                .width(70.dp)
                .align(Alignment.CenterStart)
        )

        CardItem(
            state = CardItemState(FaceDownCard),
            modifier = Modifier
                .padding(end = 56.dp)
                .width(70.dp)
                .align(Alignment.CenterEnd)
        )

        Hand(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

data class CardItemState(
    val card: ICardDrawable,
    val selected: Boolean = false,
)

@Composable
fun CardItem(state: CardItemState, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
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

@Composable
fun Hand(modifier: Modifier = Modifier) {
    val cardList  = remember {
        mutableStateListOf(
            CardItemState(
                card = Card(CardRank.THREE, CardSuit.DIAMOND),
                selected = false
            ),
            CardItemState(
                Card(CardRank.FOUR, CardSuit.SPADE),
                selected = false
            ),
            CardItemState(
                Card(CardRank.FIVE, CardSuit.SPADE),
                selected = false
            ),
            CardItemState(
                Card(CardRank.QUEEN, CardSuit.CLUB),
                selected = false
            ),
            CardItemState(
                Card(CardRank.KING, CardSuit.DIAMOND),
                selected = false
            ),
            CardItemState(
                Card(CardRank.ACE, CardSuit.HEART),
                selected = false
            ),
            CardItemState(
                Card(CardRank.SEVEN, CardSuit.DIAMOND),
                selected = false
            ),
            CardItemState(
                Card(CardRank.SEVEN, CardSuit.CLUB),
                selected = false
            ),
            CardItemState(
                Card(CardRank.TWO, CardSuit.HEART),
                selected = false
            ),
            CardItemState(
                Card(CardRank.TWO, CardSuit.SPADE),
                selected = false
            ),
            CardItemState(
                Card(CardRank.TEN, CardSuit.HEART),
                selected = false
            ),
            CardItemState(
                Card(CardRank.NINE, CardSuit.CLUB),
                selected = false
            ),
            CardItemState(
                Card(CardRank.THREE, CardSuit.SPADE),
                selected = false
            )
        )
    }

    val cardWidth = 60.dp
    val overlapOffset = (0.55*cardWidth.value).dp
    val totalWidth = ((cardList.size - 1) * overlapOffset.value).dp + cardWidth
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Column (
        modifier = modifier.fillMaxWidth(),
    ) {
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
                onClick = {},
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
                onClick = {}
            ) {
                Text("Đánh")
            }

        }
        Box(
            modifier = Modifier.offset(x=(screenWidth/2 - totalWidth.value/2).dp)
        ) {
            cardList.forEachIndexed { index, cardState ->
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
                        .zIndex(index.toFloat())
                ) {
                    cardList[index] = cardList[index].copy(
                        selected = !cardList[index].selected
                    )
                }
            }
        }

    }
}
/*
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
    val maxRotation = 5f // Góc xoay tối đa
    val maxOffsetY = 15.dp // Độ cong tối đa
    val midIndex = cardList.size / 2 // Vị trí trung tâm

    val offsetTotalWidth = ((cardList.size - 1) * overlapOffset.value).dp / 2

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        cardList.forEachIndexed { index, card ->
            val relativeIndex = index - midIndex
            val rotation = relativeIndex * (maxRotation / midIndex)
            val offsetY = (maxOffsetY.value - abs(relativeIndex) * (maxOffsetY.value / midIndex)).dp

            CardItem(
                card = card,
                modifier = Modifier
                    .width(cardWidth)
                    .offset(
                        x = (index * overlapOffset.value).dp - offsetTotalWidth, // Căn giữa bộ bài
                        y = -offsetY // Làm cong lên
                    )
                    .zIndex(index.toFloat())
                    .graphicsLayer(rotationZ = rotation)
            )
        }
    }
}
 */

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
