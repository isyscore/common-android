@file:Suppress("DuplicatedCode", "unused", "PropertyName")

package com.isyscore.kotlin.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class Assets {
    var src = ""
    /**
     * dest can be path(String), file(File), stream(OutputStream), bytearray(ByteArray)
     */
    var dest: Any? = null
    var isDestText = false
    var _result:(Boolean, String?, String?) -> Unit = { _, _, _ -> }
    fun result(r:(status: Boolean, text: String?, errMsg: String?) -> Unit) {
        _result = r
    }
}

fun Context.assetsIO(init: Assets.() -> Unit) {
    val a = Assets()
    a.init()
    AssetsOperations.assetsIO(this, a.src, a.dest, a.isDestText, a._result)
}

fun Context.assetsBitmap(src: String): Bitmap = BitmapFactory.decodeStream(this.assets.open(src))

fun Context.assetsDrawable(src: String): Drawable = BitmapDrawable(this.resources, assetsBitmap(src))

fun Context.assetsReadText(srcFile: String): String {
    var ret = ""
    assetsIO {
        src = srcFile
        isDestText = true
        result { status, text, _ ->
            if (status) {
                ret = text!!
            }
        }
    }
    return ret
}

fun Context.assetsReadBytes(srcFile: String): ByteArray = assets.open(srcFile).use { it.readBytes() }

private object AssetsOperations {

    fun assetsIO(context: Context, src: String, dest: Any?, isDestText: Boolean, callback:(Boolean, String?, String?) -> Unit) {
        context.assets.open(src).use {
            if (isDestText) {
                try {
                    callback(true, String(it.readBytes()), null)
                } catch (e: Exception) {
                    callback(false, null, e.message)
                }
            } else {
                when(dest) {
                    is String -> {
                        try {
                            FileOutputStream(dest).use { f -> it.copyTo(f); f.flush() }
                            callback(true, null, null)
                        } catch (e: Exception) {
                            callback(false, null, e.message)
                        }
                    }
                    is File -> {
                        try {
                            FileOutputStream(dest).use { f -> it.copyTo(f); f.flush() }
                            callback(true, null, null)
                        } catch (e: Exception) {
                            callback(false, null, e.message)
                        }
                    }
                    is OutputStream -> {
                        try {
                            it.copyTo(dest)
                            callback(true, null, null)
                        } catch (e: Exception) {
                            callback(false, null, e.message)
                        }
                    }
                }
            }
        }
    }

}