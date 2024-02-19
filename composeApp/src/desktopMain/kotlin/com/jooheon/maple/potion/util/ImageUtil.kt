package com.jooheon.maple.potion.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import org.bytedeco.javacv.Java2DFrameUtils
import org.bytedeco.opencv.opencv_core.Mat

object ImageUtil {

    fun loadImageResource(name: String): ImageBitmap {
        return useResource(name) { loadImageBitmap(it) }
    }

    fun ImageBitmap.toMat(): Mat = Java2DFrameUtils.toMat(this.toAwtImage())
}