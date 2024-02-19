package com.jooheon.maple.potion.display

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.jooheon.maple.potion.util.ImageUtil
import com.jooheon.maple.potion.util.ImageUtil.toMat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.bytedeco.javacpp.indexer.FloatIndexer
import org.bytedeco.javacv.Java2DFrameUtils
import org.bytedeco.opencv.global.opencv_core.CV_32FC1
import org.bytedeco.opencv.global.opencv_core.CV_8UC1
import org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY
import org.bytedeco.opencv.global.opencv_imgproc.TM_CCOEFF_NORMED
import org.bytedeco.opencv.global.opencv_imgproc.cvtColor
import org.bytedeco.opencv.global.opencv_imgproc.matchTemplate
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Point
import java.awt.Robot
import java.awt.image.BufferedImage


class DisplayProvider(
    private val robot: Robot
) {
    private var isActive = false

    private lateinit var hpTemplateImage: Mat
    private lateinit var mpTemplateImage: Mat

    private val _model = MutableStateFlow(DisplayModel.default)
    val model = _model.asStateFlow()

    init {
        initMatchingImages()
    }

    suspend fun start() = withContext(Dispatchers.IO) {
        isActive = true
        while (isActive) {
            val screenImage = robot.createScreenCapture(DisplayModel.screenRect)

            val matchingPoints = listOf(
                hpTemplateImage,
                mpTemplateImage
            ).map { template ->
                async {
                    templateMatching(
                        template = template,
                        screenImage = screenImage
                    )
                }
            }

            val pointImages = matchingPoints.map {
                cropImage(
                    image = screenImage,
                    point = it.await()
                )
            }

            _model.update {
                it.copy(
                    screenImage = screenImage.toComposeImageBitmap(),
                    hpImage = pointImages.firstOrNull() ?: DisplayModel.defaultImage,
                    mpImage = pointImages.lastOrNull() ?: DisplayModel.defaultImage
                )
            }
        }
    }

    fun stop() {
        isActive = false
        _model.tryEmit(DisplayModel.default)
    }

    private fun cropImage(image: BufferedImage, point: Point?): ImageBitmap? {
        if(point == null) return null
        return try {
            image.getSubimage(point.x(), point.y(), 112, 20).toComposeImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    private fun templateMatching(template: Mat, screenImage: BufferedImage): Point? {
        val screenMat = Java2DFrameUtils.toMat(screenImage)
        val screenGray = Mat(screenMat.size(), CV_8UC1)
        cvtColor(screenMat, screenGray, COLOR_BGR2GRAY)

        val result = Mat(
            /** rows **/ screenGray.rows() - template.rows() + 1,
            /** cols **/ screenGray.cols() - template.cols() + 1,
            /** type **/ CV_32FC1
        )

        matchTemplate(screenGray, template, result, TM_CCOEFF_NORMED)

        return getPointFromMatAboveThreshold(result, 0.75f)
    }

    private fun getPointFromMatAboveThreshold(mat: Mat, threshold: Float): Point? {
        var point: Point? = null
        val indexer = mat.createIndexer<FloatIndexer>()

        var maxMatch = 0f
        for (y in 0 until mat.rows()) {
            for (x in 0 until mat.cols()) {
                val match = indexer[y.toLong(), x.toLong()]
                if (match > threshold && match > maxMatch) {
                    maxMatch = match
                    point = Point(x, y)
                }
            }
        }

        return point
    }

    private fun initMatchingImages() {
        val hpImage = ImageUtil.loadImageResource("health_image.png").toMat()
        val mpImage = ImageUtil.loadImageResource("magic_image.png").toMat()

        val hpGrayImage = Mat(hpImage.size(), CV_8UC1)
        cvtColor(hpImage, hpGrayImage, COLOR_BGR2GRAY)

        val mpGrayImage = Mat(hpImage.size(), CV_8UC1)
        cvtColor(mpImage, mpGrayImage, COLOR_BGR2GRAY)

        this.hpTemplateImage = hpGrayImage
        this.mpTemplateImage = mpGrayImage
    }
}