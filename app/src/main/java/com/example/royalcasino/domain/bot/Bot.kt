package com.example.royalcasino.domain.bot

import com.example.royalcasino.domain.core.card.Card
import com.example.royalcasino.domain.core.card.combination.CardCombination
import com.example.royalcasino.domain.core.card.rank.CardRank
import com.example.royalcasino.domain.core.hand.Hand
import com.example.royalcasino.domain.core.turn.Turn

abstract class Bot {
    protected lateinit var hand: Hand
    fun takeHand(hand: Hand) : Bot {
        this.hand = hand
        return this
    }
    fun makeTurn(opponentTurn: Turn?) : Turn {
        if (opponentTurn == null) return startNewRoundWithTurn()
        return followTurn(opponentTurn)
    }
    protected abstract fun followTurn(opponentTurn: Turn): Turn
    protected abstract fun startNewRoundWithTurn() : Turn
    protected fun calculateStraightValueArray(cards: List<Card>) : IntArray {
        val straightLengthArr = IntArray(cards.size) { 0 }

        straightLengthArr[straightLengthArr.size - 1] = if (cards.last().rank != CardRank.TWO) 1 else 0

        for (i in cards.size - 2 downTo 0) {
            if (cards[i].rank.ordinal == cards[i + 1].rank.ordinal - 1 && cards[i + 1].rank != CardRank.TWO) {
                straightLengthArr[i] = straightLengthArr[i + 1] + 1
            } else if (cards[i].rank == cards[i + 1].rank) {
                straightLengthArr[i] = straightLengthArr[i + 1]
            } else {
                straightLengthArr[i] = 1
            }
        }

        return straightLengthArr
    }
    protected fun calculatePairValueArray(cards: List<Card>) : IntArray {
        val pairValueArr = IntArray(cards.size) { 0 }
        if (cards[cards.size - 1].rank != CardRank.TWO &&
            cards[cards.size - 1].rank == cards[cards.size - 2].rank
        ) {
            pairValueArr[pairValueArr.size - 1] = 1
        }

        for (i in cards.size - 2 downTo 1) {
            if (cards[i].rank == cards[i + 1].rank) {
                pairValueArr[i] = pairValueArr[i + 1]
            } else if (cards[i].rank == cards[i - 1].rank) {
                if (cards[i].rank.ordinal == cards[i + 1].rank.ordinal - 1) {
                    pairValueArr[i] = pairValueArr[i + 1] + 1
                } else {
                    pairValueArr[i] = 1
                }
            } else {
                pairValueArr[i] = 0
            }
        }

        return pairValueArr
    }
    private fun getConsecutivePairsCombinationOrNull(cards: List<Card>, numOfPairs: Int) : CardCombination? {
        if (numOfPairs != 4 && numOfPairs != 3) {
            throw IllegalArgumentException("Invalid number of pairs: $numOfPairs. Only 3 or 4 consecutive pairs are allowed.")
        }
        val pairValueArr = calculatePairValueArray(cards)
        val beginIndex = pairValueArr.indexOf(numOfPairs)

        if (beginIndex == -1) return null

        val combination = CardCombination()
        combination.addCard(cards[beginIndex])
        var prevCard = cards[beginIndex]
        var index = beginIndex + 1
        var needIncreaseRank = false

        while (index < cards.size &&
            cards[index].rank.ordinal - cards[beginIndex].rank.ordinal < numOfPairs
        ) {
            if (cards[index].rank != prevCard.rank) {
                combination.addCard(cards[index])
                prevCard = cards[index]
                needIncreaseRank = false
            } else if (needIncreaseRank == false) {
                combination.addCard(cards[index])
                prevCard = cards[index]
                needIncreaseRank = true
            }
            index++
        }

        return combination
    }
    protected fun get3ConsecutivePairsCombinationOrNull(cards: List<Card>) : CardCombination? {
        return getConsecutivePairsCombinationOrNull(cards, 3)
    }
    protected fun get4ConsecutivePairsCombinationOrNull(cards: List<Card>) : CardCombination? {
        return getConsecutivePairsCombinationOrNull(cards, 4)
    }
    protected fun getFourOfAKindsOrNull(cards: List<Card>) : List<CardCombination>? {
        if (cards.size < 4) return null

        val result = mutableListOf<CardCombination>()
        var index = 3

        while (index < cards.size) {
            if (cards[index].rank == cards[index-1].rank &&
                cards[index].rank == cards[index-2].rank &&
                cards[index].rank == cards[index-3].rank
            ) {
                val combination = CardCombination()
                combination.addCard(cards[index])
                combination.addCard(cards[index-1])
                combination.addCard(cards[index-2])
                combination.addCard(cards[index-3])

                result.add(combination)

                index += 4

            } else {
                index++
            }
        }

        if (result.isEmpty()) return null

        return result.toList()
    }
    protected fun getPairsOrNull(cards: List<Card>) : List<CardCombination>? {
        if (cards.size < 2) return null
        val result = mutableListOf<CardCombination>()
        var index = 1
        while (index < cards.size) {
            if (cards[index].rank == cards[index - 1].rank) {
                val pair = CardCombination()
                pair.addCard(cards[index-1])
                pair.addCard(cards[index])
                result.add(pair)
                index += 2
            } else {
                index++
            }
        }

        return if (!result.isEmpty()) result.toList() else null
    }
    protected fun getRemainingCardsAfterExcludingUltimateCombination(cards: List<Card>) : List<Card> {
        /*
        Order by priority:
            1. Four of a kind + four of a kind
            2. Four of a kind + three consecutive pairs
            3. Four consecutive pairs
            4. Four of a kind
            5. Three consecutive pairs
         */
        var remainingCard: List<Card>
        val fourOfAKindList = getFourOfAKindsOrNull(cards)

        // 1. Four of a kind + four of a kind
        if (fourOfAKindList?.size == 2) {
            remainingCard = cards
                .filter { !fourOfAKindList[0].getAllCards().contains(it) }
                .filter { !fourOfAKindList[1].getAllCards().contains(it) }

            return remainingCard
        }

        // 2. Four of a kind + three consecutive pairs
        if (fourOfAKindList?.size == 1) {
            val cardsInFourOfAKind = fourOfAKindList.first().getAllCards()
            val cardsExcludeFourOfAKind = cards.filter { !cardsInFourOfAKind.contains(it) }
            val threeConsecutivePair = get3ConsecutivePairsCombinationOrNull(cardsExcludeFourOfAKind)
            threeConsecutivePair?.let {
                val cardsInThreeConsecutivePairs = threeConsecutivePair.getAllCards()
                remainingCard = cardsExcludeFourOfAKind.filter { card-> !cardsInThreeConsecutivePairs.contains(card) }
                return remainingCard
            }
        }

        // 3. Four consecutive pairs
        val fourConsecutivePairs = get4ConsecutivePairsCombinationOrNull(cards)
        fourConsecutivePairs?.let {
            val cardsInFourConsecutivePairs = it.getAllCards()
            remainingCard = cards.filter { card-> !cardsInFourConsecutivePairs.contains(card) }
            return remainingCard
        }

        // 4. Four of a kind
        if (fourOfAKindList?.size == 1) {
            val cardsInFourOfAKind = fourOfAKindList[0].getAllCards()
            remainingCard = cards.filter { !cardsInFourOfAKind.contains(it) }
            return remainingCard
        }

        // 5. Three consecutive pairs
        val threeConsecutivePairs = get3ConsecutivePairsCombinationOrNull(cards)
        threeConsecutivePairs?.let {
            val cardsInThreeConsecutivePairs = it.getAllCards()
            remainingCard = cards.filter { card-> !cardsInThreeConsecutivePairs.contains(card) }
            return remainingCard
        }

        return cards
    }
    protected fun getThreeOfAKindsOrNull(cards: List<Card>): List<CardCombination>? {
        if (cards.size < 3) return null

        val result = mutableListOf<CardCombination>()
        var index = 2

        while (index < cards.size) {
            if (cards[index].rank == cards[index-1].rank &&
                cards[index].rank == cards[index-2].rank
            ) {
                val combination = CardCombination()
                combination.addCard(cards[index])
                combination.addCard(cards[index-1])
                combination.addCard(cards[index-2])

                result.add(combination)

                index += 3

            } else {
                index++
            }
        }

        if (result.isEmpty()) return null

        return result.toList()
    }
    protected fun getStraightOrNull(cards: List<Card>, startIndex: Int, straightSize: Int) : CardCombination? {
    if (startIndex < 0 || startIndex >= cards.size) return null
    if (straightSize < 3 || straightSize > 11) return null
    if (startIndex + straightSize > cards.size) return null

    var combination = CardCombination()
    combination.addCard(cards[startIndex])
    var prevCard = cards[startIndex]
    var index = startIndex + 1

    while (index < cards.size && cards[index].rank.ordinal - cards[startIndex].rank.ordinal < straightSize) {
        if (cards[index].rank == CardRank.TWO) return null
        when (cards[index].rank.ordinal - prevCard.rank.ordinal) {
            0 -> {}
            1 -> {
                if (cards[index].rank.ordinal - cards[startIndex].rank.ordinal == straightSize - 1) {
                    if (index + 1 == cards.size || cards[index + 1].rank != cards[index].rank) {
                        combination.addCard(cards[index])
                        break
                    }
                } else {
                    combination.addCard(cards[index])
                    prevCard = cards[index]
                }
            }
            else -> { return null }
        }
        index++
    }

    return if (combination.size == straightSize) combination else null
    }

}
