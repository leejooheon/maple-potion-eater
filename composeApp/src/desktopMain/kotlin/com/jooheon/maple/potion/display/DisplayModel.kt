package com.jooheon.maple.potion.display

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.image.BufferedImage

data class DisplayModel(
    val screenImage: ImageBitmap,
    val hpImage: ImageBitmap,
    val mpImage: ImageBitmap,
) {
    companion object {
        val defaultImage = BufferedImage(128, 128, BufferedImage.TYPE_BYTE_BINARY).toComposeImageBitmap()
        val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize)
        val default = DisplayModel(
            screenImage = defaultImage,
            hpImage = defaultImage,
            mpImage = defaultImage
        )
    }
}