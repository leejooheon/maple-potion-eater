package com.jooheon.maple.potion.automatic

import com.jooheon.maple.potion.health.HealthModel
import com.jooheon.maple.potion.health.RingBuffer
import com.jooheon.maple.potion.setting.SettingModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Robot
import java.awt.event.KeyEvent
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

class PotionEater(
    private val robot: Robot,
    private val scope: CoroutineScope,
    private val healthState: SharedFlow<HealthModel>,
    private val settingState: StateFlow<SettingModel>,
) {
    private val hpBuffer = RingBuffer(SIZE) { settingState.value.fullHp.toFloat() }
    private val mpBuffer = RingBuffer(SIZE) { settingState.value.fullMp.toFloat() }

    private val _hpFilterState = MutableStateFlow(Pair(0, 0))
    val hpFilterState = _hpFilterState.asStateFlow()

    private val _mpFilterState = MutableStateFlow(Pair(0, 0))
    val mpFilterState = _mpFilterState.asStateFlow()

    init {
        collectHealthState()
    }

    private fun collectHealthState() = scope.launch {
        healthState.collectLatest {
            val state = settingState.value

            val hpPoint = it.hpPoint.toFloat()
            val hpFilter = filter(
                state = _hpFilterState,
                buffer = hpBuffer,
                value = hpPoint,
                max = settingState.value.fullHp.toFloat(),
                multiple = HP_MULTIPLE
            )

            if(hpFilter) {
                val percent = ((hpPoint / state.fullHp.toFloat()) * 100f).toInt()
                println("HP: percent: $percent, target: ${state.hpEatPercentage} ")

                maybeEatPotion(
                    percentage = percent,
                    targetPercentage = state.hpEatPercentage,
                    keyEvent = KeyEvent.VK_PAGE_UP
                )
            }

            val mpPoint = it.mpPoint.toFloat()
            if(it.mpPoint != HealthModel.defaultPoint) mpBuffer.append(mpPoint)

            val mpFilter = filter(
                state = _mpFilterState,
                buffer = mpBuffer,
                value = mpPoint,
                max = settingState.value.fullMp.toFloat(),
                multiple = MP_MULTIPLE
            )

            if(mpFilter) {
                val percent = ((mpPoint / state.fullMp.toFloat()) * 100f).toInt()
                println("MP: percent: $percent, target: ${state.mpEatPercentage} ")

                maybeEatPotion(
                    percentage = percent,
                    targetPercentage = state.mpEatPercentage,
                    keyEvent = KeyEvent.VK_PAGE_DOWN
                )
            }
        }
    }

    private fun filter(
        state: MutableStateFlow<Pair<Int, Int>>,
        buffer: RingBuffer<Float>,
        value: Float,
        max: Float,
        multiple: Float,
    ): Boolean {
        if(value == HealthModel.defaultPoint.toFloat()) return false

        var sum = 0f
        repeat(buffer.size) { buffer.getOrNull(it)?.let { sum += it } }
        val average = sum / buffer.size.toFloat()
        val minimumValue = average / multiple
        val maximumValue = min(average * multiple, max)

        println("PotionEat Filter: $average[min: $minimumValue, max: $maximumValue] -> $value")
        if(minimumValue > value) return false
        if(maximumValue < value) return false

        state.tryEmit(Pair(minimumValue.toInt(), maximumValue.toInt()))
        buffer.append(value)
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
        private const val SIZE = 20
        private const val MP_MULTIPLE = 1.3f
        private const val HP_MULTIPLE = 2f
    }

}