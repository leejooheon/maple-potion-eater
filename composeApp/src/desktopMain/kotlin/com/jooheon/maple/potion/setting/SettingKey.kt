package com.jooheon.maple.potion.setting

sealed class SettingKey(val key: String) {
    data object FullHP: SettingKey("FullHP")
    data object FullMP: SettingKey("FullMP")
    data object HpEatPercentage: SettingKey("HpEatPercentage")
    data object MpEatPercentage: SettingKey("MpEatPercentage")
    data object TesseractPath: SettingKey("TesseractPath")

    companion object {
        val keySet = listOf(
            FullHP, FullMP, HpEatPercentage ,MpEatPercentage ,TesseractPath
        )
    }
}