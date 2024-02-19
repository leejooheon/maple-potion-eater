package com.jooheon.maple.potion.health


data class HealthModel(
    val hpPoint: Int,
    val mpPoint: Int,
) {
    companion object {
        const val defaultPoint = -1
        val default = HealthModel(
            hpPoint = defaultPoint,
            mpPoint = defaultPoint,
        )
        const val fullHpPoint = 512
        const val fullMpPoint = 1024
    }
}