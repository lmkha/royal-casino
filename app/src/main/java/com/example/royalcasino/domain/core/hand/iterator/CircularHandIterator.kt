package com.example.royalcasino.domain.core.hand.iterator

import com.example.royalcasino.domain.core.hand.Hand

class CircularHandIterator(hands: List<Hand>, startIndex: Int) : HandIterator {
    private var index = startIndex
    private var hands: MutableList<Hand> = hands.toMutableList()
    override fun next(): Hand {
        if (hands.isEmpty()) throw NoSuchElementException("There is no hands in hand collection!")
        val hand = hands[index]
        index = (index + 1) % hands.size
        return hand
    }

    override fun hasNext(): Boolean {
        return hands.isEmpty()
    }
}
