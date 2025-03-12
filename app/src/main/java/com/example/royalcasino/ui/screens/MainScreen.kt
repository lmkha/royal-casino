package com.example.royalcasino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.royalcasino.domain.model.card.FaceDownCard
import com.example.royalcasino.ui.components.CardItem
import com.example.royalcasino.ui.components.CurrentTurn
import com.example.royalcasino.ui.components.Hand
import com.example.royalcasino.ui.components.PreviousTurn
import com.example.royalcasino.viewmodel.CardState
import com.example.royalcasino.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {

        if (!uiState.started) {
            Button(
                onClick = { viewModel.startGame() },
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Text("Start game")
            }
        }

        if (uiState.started) {
            CurrentTurn(
                turn = uiState.currentTurn,
                modifier = Modifier
                    .align(Alignment.Center)
            )

            PreviousTurn(
                turn = uiState.previousTurn,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = 30.dp,
                        y = -(15).dp
                    )
            )
        }

        Text(
            text = (uiState.remainingTimeForTurn / 1000L).toString(),
            modifier = Modifier
                .padding(top = 24.dp, end = 24.dp)
                .align(Alignment.TopEnd)
        )

        CardItem(
            state = CardState(FaceDownCard),
            modifier = Modifier
                .padding(top = 24.dp)
                .width(70.dp)
                .align(Alignment.TopCenter)
        )

        CardItem(
            state = CardState(FaceDownCard),
            modifier = Modifier
                .padding(start = 56.dp)
                .width(70.dp)
                .align(Alignment.CenterStart)
        )

        CardItem(
            state = CardState(FaceDownCard),
            modifier = Modifier
                .padding(end = 56.dp)
                .width(70.dp)
                .align(Alignment.CenterEnd)
        )

        Hand(
            cardsInHand = uiState.cardStates,
            isMyTurn = uiState.isMyTurn,
            onCardClick = { index -> viewModel.clickCard(index) },
            onPlayTurn = { viewModel.playTurn() },
            onSkipTurn = { viewModel.skipTurn() },
            modifier = Modifier
                .padding(bottom = 24.dp)
                .align(Alignment.BottomCenter),
        )
    }
}

@Preview( name = "Landscape",
    showBackground = true,
    widthDp = 915,
    heightDp = 411,
)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
