package com.jooheon.maple.potion.setting

sealed class SettingEvent {
    class OnSaveClick(val model: SettingModel): SettingEvent()
}