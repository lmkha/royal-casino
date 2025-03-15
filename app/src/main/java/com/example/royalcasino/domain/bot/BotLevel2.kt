package com.example.royalcasino.domain.bot

import com.example.royalcasino.domain.core.turn.Turn
import com.example.royalcasino.domain.core.turn.TurnAction

class BotLevel2 : BotLevel1() {
    override fun startNewRoundWithTurn(): Turn {
        /*
        Order of priority combination after excluding ultimate combination
            1. Three of a kind
            2. Straight
            3. Pair
            4. Single
         */

        val cards = getRemainingCardsAfterExcludingUltimateCombination(hand.getCardsInHand())

        // 1.Three of a kind
        val threeOfAKindList = getThreeOfAKindsOrNull(cards)
        threeOfAKindList?.size?.let {
            if (it > 0) {
                hand.applyCombination(threeOfAKindList[0])
                return hand.submitTurn(TurnAction.PLAY)
            }
        }

        // 2. Straight
        val straightSizes = calculateStraightValueArray(cards)
        val sizeOfLongestStraight = straightSizes.max()
        if (sizeOfLongestStraight > 3) {
            val startIndexOfLongestStraight = straightSizes.indexOf(sizeOfLongestStraight)
            val straight = getStraightOrNull(cards, startIndexOfLongestStraight, sizeOfLongestStraight)
            straight?.let {
                hand.applyCombination(it)
                return hand.submitTurn(TurnAction.PLAY)
            }
        }

        // 3. Pair
        val pairs = getPairsOrNull(cards)
        pairs?.let {
            if (!it.isEmpty()) {
                hand.applyCombination(it[0])
                return hand.submitTurn(TurnAction.PLAY)
            }
        }

        // 4. Single
        return super.startNewRoundWithTurn()
    }
}