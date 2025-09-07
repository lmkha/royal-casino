package com.example.royalcasino.games.thirteen.core

import com.example.royalcasino.core.Deck
import com.example.royalcasino.core.card.Card
import com.example.royalcasino.core.card.CardRank
import com.example.royalcasino.core.card.CardSuit
import com.example.royalcasino.core.player.Player
import com.example.royalcasino.games.Game
import com.example.royalcasino.games.thirteen.bot.Bot
import com.example.royalcasino.games.thirteen.bot.BotLevel2
import com.example.royalcasino.games.thirteen.core.combination.CardCombination
import com.example.royalcasino.games.thirteen.core.combination.CardCombinationType
import com.example.royalcasino.games.thirteen.core.turn.Turn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.forEach

class ThirteenGame(players: List<Player>) : Game {
    private lateinit var _deck: Deck
    private lateinit var _bot: Bot
    private lateinit var _handWonPreviousRound: Hand
    private var _isFirstGame: Boolean = true
    private var _hands: MutableList<Hand> = mutableListOf()
    private var _result: MutableList<Player> = mutableListOf()
    private val _currentRound: MutableStateFlow<Round?> = MutableStateFlow(null)
    private val _numberOfRemainingCards: MutableStateFlow<List<Int>> = MutableStateFlow(emptyList())
    private val _isOver: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val myHand: Hand get() = _hands[0]
    val currentRound: StateFlow<Round?> = _currentRound.asStateFlow()
    val numberOfRemainingCards: StateFlow<List<Int>> = _numberOfRemainingCards.asStateFlow()
    val isOver: StateFlow<Boolean> = _isOver.asStateFlow()

    init {
        players.forEach { _hands.add(Hand(it)) }
    }

    override fun setup() {
        if (_hands.size < 2 || _hands.size > 4) throw Exception("Number of players must be between 2 and 4")
        _deck = Deck.newInstance().apply { shuffle() }

        val cardsOfDeck = _deck.getDevDeck01Original().toMutableList()
//        _deck.shuffle()
//        val cardsOfDeck = _deck.getCards().toMutableList()

        _hands.forEach { hand->
            cardsOfDeck.take(13).let {drawnCards ->
                hand.receiveCards(drawnCards)
                hand.sortCardsInHand()
                cardsOfDeck.removeAll(drawnCards)
            }
        }
//        _bot = BotLevel1()
        _bot = BotLevel2()
        _handWonPreviousRound = _hands.find { it.getCardsInHand().contains(
            Card(
                CardRank.THREE,
                CardSuit.SPADE
            )
        ) }!!
        updateNumbersOfRemainingCard()
    }

    override fun start() {
        checkAndHandleAutoWin()
        startNewRound()
    }

    fun processTurn(turn: Turn) {
        _currentRound.value?.processTurn(turn)
    }

    fun pause() {
        _currentRound.value?.pause()
    }

    fun resume() {
        _currentRound.value?.resume()
    }

    private fun startNewRound() {
        _currentRound.value = Round(
            _hands = _hands.filter { it.numberOfRemainingCards > 0 },
            startHand = _handWonPreviousRound,
            _bot = _bot,
            _callback = object : Round.Callback {
                override fun onTurnFinished() {
                    updateNumbersOfRemainingCard()
                }

                override fun onRoundEnd(wonHand: Hand) {
                    _handWonPreviousRound = wonHand
                    startNewRound()
                }

                override fun onHandFinished(hand: Hand) {
                    handleHandFinished(hand)
                }
            },
        ).also {
            it.start()
        }
    }

    private fun updateNumbersOfRemainingCard() {
        _numberOfRemainingCards.value = _hands.map { it.numberOfRemainingCards }.toList()
    }

    private fun handleHandFinished(hand: Hand) {
        _result.add(hand.owner)
        // Game over, add the remaining hand to to end of result list
        if (_result.size == _hands.size - 1) {
            handleGameOver()
        }
    }

    private fun handleGameOver() {
        val remainingHandOwner = _hands.find { it.numberOfRemainingCards != 0 }?.owner
        if (remainingHandOwner != null) {
            _result.add(remainingHandOwner)
        }
        showResult()
    }

    private fun showResult() {
        println("Game over!!!\nResult:")
        _result.forEachIndexed { index, player ->
            println("Rank ${index+1}: ${player.name}")
        }
    }

    private fun checkAndHandleAutoWin() {
        // Make sure that cards in hand were sorted
        /*
        If this is the first game:
            + Consecutive pairs include Card(CardRank.THREE, CardSuit.SPADE)
            + 4 of a kind (CardRank.THREE)
        */
        if (_isFirstGame) {
            // Cards in hand were sorted, so if all 4 beginning cards have Rank.Three, then it's auto win
            val firstGameHaveRankThreeX4Hand = _hands.find { hand->
                hand.getCardsInHand().subList(0, 4).all { it.rank == CardRank.THREE }
            }
            if (firstGameHaveRankThreeX4Hand != null) {
                _result.add(firstGameHaveRankThreeX4Hand.owner)
            }

            // Consecutive pairs include Card(CardRank.THREE, CardSuit.SPADE)
            val firstGameHaveConsecutivePairIncludeSpadeThree = _hands.find { hand->
                val sixBeginningCardsOfHand = hand.getCardsInHand().subList(0, 6)
                sixBeginningCardsOfHand.contains(Card(CardRank.THREE, CardSuit.SPADE)) &&
                        CardCombination(sixBeginningCardsOfHand).type == CardCombinationType.CONSECUTIVE_PAIRS
            }
            if (firstGameHaveConsecutivePairIncludeSpadeThree != null &&
                !_result.contains(firstGameHaveConsecutivePairIncludeSpadeThree.owner)
            ) {
                _result.add(firstGameHaveConsecutivePairIncludeSpadeThree.owner)
            }
        }

        /*
        Not the first game
           + Three of a kind x 4
           + Pair x 6
           + 5 consecutive pair
           + Four of a kind(CardRank.TWO)
           + Straight with length = 12
        */
        // Three of a kind x 4
        for (hand in _hands) {
            if (_result.contains(hand.owner)) continue
            val rankMap = mutableMapOf<CardRank, Int>()
            var threeOfAKindCount = 0
            hand.getCardsInHand().map { it.rank }.forEach { rank->
                rankMap[rank] = rankMap.getOrDefault(rank, 0) + 1
            }
            if (rankMap.size == 4) _result.add(hand.owner)
            if (rankMap.size == 5) {
                rankMap.forEach { (_, count) ->
                    if (count >= 3) threeOfAKindCount++
                }

                if (threeOfAKindCount == 4) _result.add(hand.owner)
            }
        }

        // Pair x6
        for (hand in _hands) {
            if (_result.contains(hand.owner)) continue
            val rankSet = mutableSetOf<CardRank>()
            hand.getCardsInHand().forEach { card: Card ->
                rankSet.add(card.rank)
            }
            if (rankSet.size <= 7) _result.add(hand.owner)
        }

        // 5 consecutive pairs
        for (hand in _hands) {
            if (_result.contains(hand.owner)) continue
            val rankMap = mutableMapOf<CardRank, Int>()
            hand.getCardsInHand().forEach { card->
                rankMap[card.rank] = rankMap.getOrDefault(card.rank, 0) + 1
            }
            val pairRank = rankMap.filter {
                it.value >= 2 && it.key != CardRank.TWO
            }.keys.toList().sorted()

            if (pairRank.size == 5 && pairRank[4].ordinal - pairRank[0].ordinal == 4) {
                _result.add(hand.owner)
            }

            if (pairRank.size == 6) {
                if (pairRank[4].ordinal - pairRank[0].ordinal == 4 ||
                    pairRank[5].ordinal - pairRank[1].ordinal == 4
                ) {
                    _result.add(hand.owner)
                }
            }
        }

        // Straight with length = 12
        for (hand in _hands) {
            if (_result.contains(hand.owner)) continue
            val cardsInHand = hand.getCardsInHand()
            var extraCount = 0
            for (i in 1 until cardsInHand.size) {
                if (cardsInHand[i].rank.ordinal == cardsInHand[i - 1].rank.ordinal) {
                    extraCount++
                    if (extraCount > 1) break
                }
            }
            if (extraCount <= 1) _result.add(hand.owner)
        }
    }
}