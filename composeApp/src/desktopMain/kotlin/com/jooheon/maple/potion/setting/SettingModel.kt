package com.jooheon.maple.potion.setting

data class SettingModel(
    val fullHp: Int,
    val fullMp: Int,
    val hpEatPercentage: Int,
    val mpEatPercentage: Int,
    val tesseractPath: String,
) {
    companion object {
        const val defaultValue = -1
        val default = SettingModel(
            fullHp = defaultValue,
            fullMp = defaultValue,
            hpEatPercentage = defaultValue,
            mpEatPercentage = defaultValue,
            tesseractPath = ""
        )
        // mac: "/opt/homebrew/Cellar/tesseract/5.3.4/share/tessdata"
    }
}