package com.example.royalcasino.domain.utils

interface ComparatorStrategy<T> {
    fun compare(a: T, b: T): Int
}
