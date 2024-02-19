package com.jooheon.maple.potion.setting

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.prefs.Preferences

class SettingProvider {
    private val preferences: Preferences = Preferences.userRoot().node(this::class.java.name)

    private val _model = MutableStateFlow(SettingModel.default)
    val model = _model.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val fullHp = preferences.getInt(SettingKey.FullHP.key, SettingModel.defaultValue)
        val fullMp = preferences.getInt(SettingKey.FullMP.key, SettingModel.defaultValue)
        val hpEatPercentage = preferences.getInt(SettingKey.HpEatPercentage.key, SettingModel.defaultValue)
        val mpEatPercentage = preferences.getInt(SettingKey.MpEatPercentage.key, SettingModel.defaultValue)
        val tesseractPath = preferences.get(SettingKey.TesseractPath.key, "")

        _model.update {
            SettingModel(fullHp, fullMp, hpEatPercentage, mpEatPercentage, tesseractPath)
        }
    }

    fun dispatch(event: SettingEvent) {
        when(event) {
            is SettingEvent.OnSaveClick -> {
                val newModel = event.model
                preferences.putInt(SettingKey.FullHP.key, newModel.fullHp)
                preferences.putInt(SettingKey.FullMP.key, newModel.fullMp)
                preferences.putInt(SettingKey.HpEatPercentage.key, newModel.hpEatPercentage)
                preferences.putInt(SettingKey.MpEatPercentage.key, newModel.mpEatPercentage)
                preferences.put(SettingKey.TesseractPath.key, newModel.tesseractPath)

                loadData()
            }
        }
    }
}