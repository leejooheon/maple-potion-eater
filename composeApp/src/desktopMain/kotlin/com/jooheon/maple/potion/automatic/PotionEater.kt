package com.jooheon.maple.potion.automatic

import com.jooheon.maple.potion.health.HealthModel
import com.jooheon.maple.potion.health.RingBuffer
import com.jooheon.maple.potion.setting.SettingModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Robot
import java.awt.event.KeyEvent
import kotlin.random.Random
import kotlin.random.nextInt

class PotionEater(
    private val robot: Robot,
    private val scope: CoroutineScope,
    private val healthState: StateFlow<HealthModel>,
    private val settingState: StateFlow<SettingModel>,
) {
    private val hpBuffer = RingBuffer(SIZE) { settingState.value.fullHp.toFloat() }
    private val mpBuffer = RingBuffer(SIZE) { settingState.value.fullMp.toFloat() }

    init {
        collectHealthState()
    }

    private fun collectHealthState() = scope.launch {
        healthState.collectLatest {
            val state = settingState.value

            val hpPoint = it.hpPoint.toFloat()
            if(filter(hpBuffer, hpPoint)) {
                hpBuffer.append(hpPoint)

                maybeEatPotion(
                    percentage = ((hpPoint / state.fullHp.toFloat()) * 100f).toInt(),
                    targetPercentage = state.hpEatPercentage,
                    keyEvent = KeyEvent.VK_PAGE_UP
                )
            }

            val mpPoint = it.mpPoint.toFloat()
            if(filter(mpBuffer, mpPoint)) {
                mpBuffer.append(mpPoint)
                maybeEatPotion(
                    percentage = ((mpPoint / state.fullMp.toFloat()) * 100f).toInt(),
                    targetPercentage = state.mpEatPercentage,
                    keyEvent = KeyEvent.VK_PAGE_DOWN
                )
            }
        }
    }

    private fun filter(buffer: RingBuffer<Float>, value: Float): Boolean {
        if(value == HealthModel.defaultPoint.toFloat()) return false

        var sum = 0f
        repeat(buffer.size) { buffer.getOrNull(it)?.let { sum += it } }
        val average = sum / buffer.size.toFloat()
        val minimumValue = average / 1.5f
        val maximumValue = average * 1.5f

        println("PotionEat Filter: $average[min: $minimumValue, max: $maximumValue] -> $value")
        if(minimumValue > value) return false
        if(maximumValue < value) return false

        return true
    }


    private suspend fun maybeEatPotion(
        percentage: Int,
        targetPercentage: Int,
        keyEvent: Int
    ) {
        if(percentage < targetPercentage) {
            robot.keyPress(keyEvent)
            wait(Random.nextInt(20 .. 52))
            robot.keyRelease(keyEvent)
            wait(Random.nextInt(1234 .. 1567))
        }
    }

    private suspend fun wait(time: Int) = withContext(Dispatchers.IO) {
        delay(time.toLong())
    }

    companion object {
        private const val SIZE = 100
    }

}