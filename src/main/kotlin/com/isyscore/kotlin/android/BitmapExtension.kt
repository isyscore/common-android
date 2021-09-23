@file:Suppress("unused", "SpellCheckingInspection", "DEPRECATION")

package com.isyscore.kotlin.android

import android.graphics.*
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

fun Bitmap.roundCorner(radis: Float): Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
    val paint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }
    val rect = Rect(0, 0, width, height)
    val rectF = RectF(rect)
    Canvas(this).apply {
        drawARGB(0, 0, 0, 0)
        drawRoundRect(rectF, radis, radis, paint)
        drawBitmap(this@roundCorner, rect, rect, paint)
    }
}


fun Bitmap.blackWhite(): Bitmap = colorMatrix(
    floatArrayOf(
        0.308f, 0.609f, 0.082f, 0.0f, 0.0f,
        0.308f, 0.609f, 0.082f, 0.0f, 0.0f,
        0.308f, 0.609f, 0.082f, 0.0f, 0.0f,
        0.000f, 0.000f, 0.000f, 1.0f, 0.0f
    )
)

fun Bitmap.rotate(angle: Float): Bitmap =
    Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(angle) }, true)

enum class FlipMode { LEFTRIGHT, UPDOWN }

fun Bitmap.flip(mode: FlipMode = FlipMode.LEFTRIGHT): Bitmap = Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
    postConcat(Matrix().apply {
        setValues(floatArrayOf(-1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f))
    })
    if (mode == FlipMode.UPDOWN) {
        setRotate(180.0f, width * 1.0f / 2, height * 1.0f / 2)
    }
}, true)


fun Bitmap.colorMatrix(matrixSrc: FloatArray): Bitmap = Bitmap.createBitmap(width, height, this.config).apply {
    Canvas(this).apply {
        drawBitmap(this@colorMatrix, 0.0f, 0.0f, Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                set(matrixSrc)
            })
        })
    }
}


fun Bitmap.scale(newWidth: Float, newHeight: Float): Bitmap = Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
    postScale(newWidth * 1.0f / width, newHeight * 1.0f / height)
}, true)


fun Bitmap.blur(level: Int): Bitmap {
    val pixels = IntArray(width * height)
    val pixelsRawSource = IntArray(width * height * 3)
    val pixelsRawNew = IntArray(width * height * 3)
    getPixels(pixels, 0, width, 0, 0, width, height)
    for (k in 1..level) {
        for (i in pixels.indices) {
            pixelsRawSource[i * 3 + 0] = Color.red(pixels[i])
            pixelsRawSource[i * 3 + 1] = Color.green(pixels[i])
            pixelsRawSource[i * 3 + 2] = Color.blue(pixels[i])
        }
        var currentPixel = width * 3 + 3
        for (i in 0 until height - 3) {
            for (j in 0 until width * 3) {
                currentPixel += 1
                val sumColor =
                    pixelsRawSource[currentPixel - width * 3] + pixelsRawSource[currentPixel - 3] + pixelsRawSource[currentPixel + 3] + pixelsRawSource[currentPixel + width * 3]
                pixelsRawNew[currentPixel] = (sumColor * 1.0f / 4).roundToInt()
            }
        }
        for (i in pixels.indices) {
            pixels[i] = Color.rgb(pixelsRawNew[i * 3 + 0], pixelsRawNew[i * 3 + 1], pixelsRawNew[i * 3 + 2])
        }
    }
    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, width, 0, 0, width, height)
    }
}

fun Bitmap.reflect(refHeight: Float): Bitmap {
    val reflectionHeight = if (refHeight == 0.0f) height / 3 else (refHeight * height).toInt()
    val reflectionBitmap = Bitmap.createBitmap(this, 0, height - reflectionHeight, width, reflectionHeight, Matrix().apply {
        preScale(1.0f, -1.0f)
    }, false)
    Canvas(reflectionBitmap).apply {
        drawRect(0.0f, 0.0f, reflectionBitmap.width.toFloat(), reflectionBitmap.height.toFloat(), Paint().apply {
            isAntiAlias = true
            shader = LinearGradient(0.0f, 0.0f, 0.0f, reflectionBitmap.height.toFloat(), 0x70FFFFFF, 0x00FFFFFF, Shader.TileMode.MIRROR)
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        })
    }
    return reflectionBitmap
}

fun Bitmap.save(filename: String, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG) {
    val f = File(filename).apply { createNewFile() }
    FileOutputStream(f).use { s ->
        compress(format, 100, s)
        s.flush()
    }
}

