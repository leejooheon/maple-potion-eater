package com.jooheon.maple.potion.automatic

import com.jooheon.maple.potion.health.HealthModel
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
    init {
        collectHealthState()
    }

    private fun collectHealthState() = scope.launch {
        healthState.collectLatest {
            val state = settingState.value

            if(it.hpPoint != HealthModel.defaultPoint) {
                maybeEatPotion(
                    percentage = ((it.hpPoint.toFloat() / state.fullHp.toFloat()) * 100f).toInt(),
                    targetPercentage = state.hpEatPercentage,
                    keyEvent = KeyEvent.VK_PAGE_UP
                )
            }

            maybeEatPotion(
                percentage = ((it.mpPoint.toFloat() / state.fullMp.toFloat()) * 100f).toInt(),
                targetPercentage = state.mpEatPercentage,
                keyEvent = KeyEvent.VK_PAGE_DOWN
            )
        }
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
}