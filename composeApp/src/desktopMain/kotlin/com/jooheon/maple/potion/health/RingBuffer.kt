package com.jooheon.maple.potion.health


class RingBuffer<T>(val size: Int, init: (index: Int) -> T) {
    private val list = MutableList(size, init)

    var index = 0
        private set

    fun getOrNull(index: Int): T? = list.getOrNull(index)

    fun append(element: T) = list.set(index++ % size, element)
    fun clear(default: T) {
        repeat(size) { append(default)}
    }
}
