package com.jooheon.maple.potion.util

fun String?.toPercentRange(): Int? {
    val value = this?.toIntOrNull() ?: return null
    if(value in 1..99) {
        return value
    }
    return null
}