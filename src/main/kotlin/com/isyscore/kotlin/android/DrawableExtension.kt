@file:Suppress("unused", "DEPRECATION")

package com.isyscore.kotlin.android

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

fun Drawable.bitmap(): Bitmap = Bitmap.createBitmap(
    intrinsicWidth,
    intrinsicHeight,
    if (opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
).apply {
    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
    draw(Canvas(this))
}

fun Drawable.scale(newWidth: Float, newHeight: Float): Drawable =
    BitmapDrawable(Bitmap.createBitmap(bitmap(), 0, 0, intrinsicWidth, intrinsicHeight, Matrix().apply {
        postScale(newWidth / intrinsicWidth, newHeight / intrinsicHeight)
    }, true))

