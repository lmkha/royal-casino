package com.example.royalcasino.domain.utils

interface Iterator<T> {
    fun next(): T
    fun hasNext(): Boolean
}
