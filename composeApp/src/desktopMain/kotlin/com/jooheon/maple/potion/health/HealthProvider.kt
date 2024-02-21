package com.jooheon.maple.potion.health

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import com.jooheon.maple.potion.display.DisplayModel
import com.jooheon.maple.potion.setting.SettingModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.sourceforge.tess4j.Tesseract
import org.bytedeco.javacv.Java2DFrameUtils
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core.Mat

class HealthProvider(
    private val scope: CoroutineScope,
    private val displayState: StateFlow<DisplayModel>,
    private val settingState: StateFlow<SettingModel>,
) {
    private var tesseract: Tesseract? = null

    private val _model = MutableSharedFlow<HealthModel>()
    val model = _model.asSharedFlow()

    init {
        collectSettingModel()
        collectScreenModel()
    }

    private fun collectSettingModel() = scope.launch {
        settingState.collectLatest {  model ->
            tesseract = Tesseract().apply {
                setDatapath(model.tesseractPath)
            }
        }
    }

    private fun collectScreenModel() = scope.launch {
        displayState.collectLatest { model ->
            val tesseract = tesseract ?: return@collectLatest

            var hpPoint = findPoints(tesseract, model.hpImage) ?: HealthModel.defaultPoint
            var mpPoint = findPoints(tesseract, model.mpImage) ?: HealthModel.defaultPoint

            if(hpPoint < 100) hpPoint = HealthModel.defaultPoint
            if(mpPoint < 100) mpPoint = HealthModel.defaultPoint

            _model.emit (
                HealthModel(
                    hpPoint = hpPoint,
                    mpPoint = mpPoint,
                )
            )
        }
    }

    private fun findPoints(tesseract: Tesseract, pointImage: ImageBitmap?): Int? {
        if(pointImage == null) return null

        return try {
            val origin = tesseract.doOCR(pointImage.toAwtImage())
                .substringBefore("\n")
                .substringAfter(" ")
                .replace("(", "")
                .replace(" ", "")
            val filtered = origin.substring(0, origin.length - 1)
            val index = if(filtered.length % 2 == 0) {
                filtered.length/2 - 1
            } else {
                filtered.length/2
            }

            val result = filtered.substring(0, index)

//            println("OCR: $origin => $result")
            return result.toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }
}