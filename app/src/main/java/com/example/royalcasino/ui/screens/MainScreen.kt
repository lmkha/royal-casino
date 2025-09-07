package com.example.royalcasino.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.royalcasino.R
import com.example.royalcasino.ui.components.BotHand
import com.example.royalcasino.ui.components.CurrentTurn
import com.example.royalcasino.ui.components.Hand
import com.example.royalcasino.ui.components.PreviousTurn
import com.example.royalcasino.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val gameState by viewModel.gameState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        // Background
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.game_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        if (!gameState.started) {
            Button(
                onClick = { viewModel.startGame() },
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Text("Start game")
            }
        }

        if (gameState.started) {
            PreviousTurn(
                turn = gameState.previousTurn,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = 30.dp,
                        y = -(15).dp
                    )
            )

            CurrentTurn(
                turn = gameState.currentTurn,
                modifier = Modifier
                    .align(Alignment.Center)
            )

        }

        // Time for turn
        Text(
            text = (gameState.remainingTimeForTurn / 1000L).toString(),
            modifier = Modifier
                .padding(top = 24.dp, end = 24.dp)
                .align(Alignment.TopEnd)
        )

        // Hands
        Hand(
            cardsInHand = gameState.cardStates,
            isMyTurn = gameState.indexOfHandGoingToMakeTurn == 0,
            onCardClick = { index -> viewModel.clickCard(index) },
            onPlayTurn = { viewModel.playTurn() },
            onSkipTurn = { viewModel.skipTurn() },
            enableSkipTurn = gameState.enableSkipTurn,
            enablePlayTurn = gameState.enablePlayTurn,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .align(Alignment.BottomCenter),
        )

        BotHand(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 56.dp)
                .border(
                    width = if (gameState.indexOfHandGoingToMakeTurn == 1) 5.dp else 0.dp,
                    color = if(gameState.indexOfHandGoingToMakeTurn == 1) Color.Green else Color.Transparent
                ),
            numberOfRemainingCard = gameState.numberOfRemainingCards.getOrNull(1) ?: 0
        )

        BotHand(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
                .border(
                    width = if (gameState.indexOfHandGoingToMakeTurn == 2) 5.dp else 0.dp,
                    color = if(gameState.indexOfHandGoingToMakeTurn == 2) Color.Green else Color.Transparent
                ),
            numberOfRemainingCard = gameState.numberOfRemainingCards.getOrNull(2) ?: 0
        )

        BotHand(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 56.dp)
                .border(
                    width = if (gameState.indexOfHandGoingToMakeTurn == 3) 5.dp else 0.dp,
                    color = if(gameState.indexOfHandGoingToMakeTurn == 3) Color.Green else Color.Transparent
                ),
            numberOfRemainingCard = gameState.numberOfRemainingCards.getOrNull(3) ?: 0
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
