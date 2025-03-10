package com.example.royalcasino.domain.model.game

import com.example.royalcasino.domain.bot.Bot
import com.example.royalcasino.domain.bot.BotLevel1
import com.example.royalcasino.domain.model.Deck
import com.example.royalcasino.domain.model.card.Card
import com.example.royalcasino.domain.model.card.combination.CardCombination
import com.example.royalcasino.domain.model.card.combination.CardCombinationType
import com.example.royalcasino.domain.model.card.rank.CardRank
import com.example.royalcasino.domain.model.card.suit.CardSuit
import com.example.royalcasino.domain.model.hand.Hand
import com.example.royalcasino.domain.model.player.Player
import com.example.royalcasino.domain.model.round.Round

class Game(players: List<Player>) {
    private var isFirstGame: Boolean = true
    private lateinit var deck: Deck
    private var hands: MutableList<Hand> = mutableListOf()
    private lateinit var bot: Bot
    var currentRound: Round? = null
        private set
    private lateinit var handWonPreviousRound: Hand
    private var result: MutableList<Player> = mutableListOf()
    val yourHand: Hand
        get() = hands[0]
    val isOver: Boolean
        get() {
            return result.size == hands.size
        }
    init {
        players.forEach { hands.add(Hand(it)) }
    }

    fun setupNewGame() {
        if (hands.size < 2 || hands.size > 4) throw Exception("Number of players must be between 2 and 4")
        deck = Deck.newDeck().apply { shuffle() }

        val cardsOfDeck = deck.getDevDeck01Original().toMutableList()
        hands.forEach { hand->
            cardsOfDeck.take(13).let {drawnCards ->
                hand.receiveCards(drawnCards)
                hand.sortCardsInHand()
                cardsOfDeck.removeAll(drawnCards)
            }
        }
        bot = BotLevel1()
        handWonPreviousRound = hands.find { it.getCardsInHand().contains(Card(CardRank.THREE, CardSuit.SPADE)) }!!
    }

    fun startNewGame() {
        checkAndHandleAutoWin()
        startNewRound()
    }

    private fun startNewRound() {
        currentRound = Round(
            hands = hands.filter { it.numberOfRemainingCards > 0 },
            startHand = handWonPreviousRound,
            bot = bot,
            onRoundEnd = { wonHand ->
                handWonPreviousRound = wonHand
                startNewRound()
            },
            onHandFinished = { handFinished ->
                handleHandFinished(handFinished)
            }
        )
    }

    fun getHand(handIndex: Int): Hand {
        return hands[handIndex]
    }

    private fun handleHandFinished(hand: Hand) {
        result.add(hand.owner)
        // Game over, add the remaining hand to to end of result list
        if (result.size == hands.size - 1) {
            handleGameOver()
        }
    }

    private fun handleGameOver() {
        val remainingHandOwner = hands.find { it.numberOfRemainingCards != 0 }?.owner
        if (remainingHandOwner != null) {
            result.add(remainingHandOwner)
        }
        showResult()
    }

    private fun showResult() {
        println("Game over!!!\nResult:")
        result.forEachIndexed { index, player ->
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
        if (isFirstGame) {
            // Cards in hand were sorted, so if all 4 beginning cards have Rank.Three, then it's auto win
            val firstGameHaveRankThreeX4Hand = hands.find { hand->
                hand.getCardsInHand().subList(0, 4).all { it.rank == CardRank.THREE }
            }
            if (firstGameHaveRankThreeX4Hand != null) {
                result.add(firstGameHaveRankThreeX4Hand.owner)
            }

            // Consecutive pairs include Card(CardRank.THREE, CardSuit.SPADE)
            val firstGameHaveConsecutivePairIncludeSpadeThree = hands.find { hand->
                val sixBeginningCardsOfHand = hand.getCardsInHand().subList(0, 6)
                sixBeginningCardsOfHand.contains(Card(CardRank.THREE, CardSuit.SPADE)) &&
                        CardCombination(sixBeginningCardsOfHand).type == CardCombinationType.CONSECUTIVE_PAIRS
            }
            if (firstGameHaveConsecutivePairIncludeSpadeThree != null &&
                !result.contains(firstGameHaveConsecutivePairIncludeSpadeThree.owner)
            ) {
                result.add(firstGameHaveConsecutivePairIncludeSpadeThree.owner)
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
        for (hand in hands) {
            if (result.contains(hand.owner)) continue
            val rankMap = mutableMapOf<CardRank, Int>()
            var threeOfAKindCount = 0
            hand.getCardsInHand().map { it.rank }.forEach { rank->
                rankMap[rank] = rankMap.getOrDefault(rank, 0) + 1
            }
            if (rankMap.size == 4) result.add(hand.owner)
            if (rankMap.size == 5) {
                rankMap.forEach { (_, count) ->
                    if (count >= 3) threeOfAKindCount++
                }

                if (threeOfAKindCount == 4) result.add(hand.owner)
            }
        }

        // Pair x6
        for (hand in hands) {
            if (result.contains(hand.owner)) continue
            val rankSet = mutableSetOf<CardRank>()
            hand.getCardsInHand().forEach { card: Card ->
                rankSet.add(card.rank)
            }
            if (rankSet.size <= 7) result.add(hand.owner)
        }

        // 5 consecutive pairs
        for (hand in hands) {
            if (result.contains(hand.owner)) continue
            val rankMap = mutableMapOf<CardRank, Int>()
            hand.getCardsInHand().forEach { card->
                rankMap[card.rank] = rankMap.getOrDefault(card.rank, 0) + 1
            }
            val pairRank = rankMap.filter {
                it.value >= 2 && it.key != CardRank.TWO
            }.keys.toList().sorted()

            if (pairRank.size == 5 && pairRank[4].ordinal - pairRank[0].ordinal == 4) {
                result.add(hand.owner)
            }

            if (pairRank.size == 6) {
                if (pairRank[4].ordinal - pairRank[0].ordinal == 4 ||
                    pairRank[5].ordinal - pairRank[1].ordinal == 4
                ) {
                    result.add(hand.owner)
                }
            }
        }

        // Straight with length = 12
        for (hand in hands) {
            if (result.contains(hand.owner)) continue
            val rankSet = mutableSetOf<CardRank>()
            hand.getCardsInHand().forEach { card->
                rankSet.add(card.rank)
            }
            if (rankSet.size == 13 || (rankSet.size == 12 && !rankSet.contains(CardRank.TWO))) {
                result.add(hand.owner)
            }
        }
    }
}
