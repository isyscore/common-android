@file:Suppress("HasPlatformType", "unused", "DEPRECATION")

package com.isyscore.kotlin.android

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.widget.TextView
import android.widget.Toast

val Context.appVersionCode: Int
    get() {
        var ret = 0
        try {
            val pi = packageManager.getPackageInfo(packageName, 0)
            ret = if (Build.VERSION.SDK_INT >= 28) {
                pi.longVersionCode.toInt()
            } else {
                pi.versionCode
            }
        } catch (e: Exception) {

        }
        return ret
    }

val Context.appVersionName: String?
    get() {
        var ret: String? = null
        try {
            val pi = packageManager.getPackageInfo(packageName, 0)
            ret = pi.versionName
        } catch (e: Exception) {

        }
        return ret
    }

fun Context.readConfig(key: String, def: String?): String? = ContextOperations.pref(this).getString(key, def)
fun Context.writeConfig(key: String, value: String?) = ContextOperations.pref(this).edit().putString(key, value).apply()
fun Context.readConfig(key: String, def: Int): Int = ContextOperations.pref(this).getInt(key, def)
fun Context.writeConfig(key: String, value: Int) = ContextOperations.pref(this).edit().putInt(key, value).apply()
fun Context.readConfig(key: String, def: Float): Float = ContextOperations.pref(this).getFloat(key, def)
fun Context.writeConfig(key: String, value: Float) = ContextOperations.pref(this).edit().putFloat(key, value).apply()
fun Context.readConfig(key: String, def: Long): Long = ContextOperations.pref(this).getLong(key, def)
fun Context.writeConfig(key: String, value: Long) = ContextOperations.pref(this).edit().putLong(key, value).apply()
fun Context.readConfig(key: String, def: Boolean): Boolean = ContextOperations.pref(this).getBoolean(key, def)
fun Context.writeConfig(key: String, value: Boolean) = ContextOperations.pref(this).edit().putBoolean(key, value).apply()

fun Context.readMetaData(key: String, def: String?): String? = ContextOperations.metaData(this).getString(key, def)
fun Context.readMetaData(key: String, def: Int): Int = ContextOperations.metaData(this).getInt(key, def)
fun Context.readMetaData(key: String, def: Float): Float = ContextOperations.metaData(this).getFloat(key, def)
fun Context.readMetaData(key: String, def: Long): Long = ContextOperations.metaData(this).getLong(key, def)
fun Context.readMetaData(key: String, def: Boolean): Boolean = ContextOperations.metaData(this).getBoolean(key, def)

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT, dark: Boolean = true) =
    ExtendedToast.makeText(this, message, duration, dark).show()

fun Context.getBitmapFromAssets(path: String) = BitmapFactory.decodeStream(resources.assets.open(path))

fun Context.resStr(resId: Int): String = resources.getString(resId)
fun Context.resStr(resId: Int, vararg args: Any?): String = resources.getString(resId, *args)
fun Context.resStrArray(resId: Int): Array<String> = resources.getStringArray(resId)
fun Context.resColor(resId: Int): Int = resources.getColor(resId, theme)
fun Context.resDrawable(resId: Int): Drawable = resources.getDrawable(resId, theme)
fun Context.attrColor(resId: Int): ColorStateList? {
    val a = obtainStyledAttributes(intArrayOf(resId))
    val color = a.getColorStateList(a.getIndex(0))
    a.recycle()
    return color
}

/**
 * initUI() must be called before using any function in it.
 */
fun Context.initUI() {
    UI.dm = resources.displayMetrics
    UI.density = UI.dm.density
    UI.width = UI.dm.widthPixels
    UI.height = UI.dm.heightPixels
}

/**
 * actionbar height
 */
fun Context.actionBarHeight(): Int {
    val a = obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
    val ret = a.getDimensionPixelSize(0, -1)
    a.recycle()
    return ret
}

/**
 * statusbar height
 */
fun Context.statusBarHeight(): Int =
    with(resources) { getDimensionPixelSize(getIdentifier("status_bar_height", "dimen", "android")) }

/**
 * navigationbar height
 */
fun Context.navigationBarHeight(): Int =
    with(resources) { getDimensionPixelSize(getIdentifier("navigation_bar_height", "dimen", "android")) }

/**
 * navigationbar exists?
 * @comment only work on nexus devices
 */
fun Context.hasNavigationBar(): Boolean = if (Build.MODEL.toLowerCase().contains("nexus")) {
    (!ViewConfiguration.get(this).hasPermanentMenuKey() && !KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK))
} else false

fun Context.goService(intent: Intent): ComponentName? = if (Build.VERSION.SDK_INT >= 26) {
    startForegroundService(intent)
} else {
    startService(intent)
}

private object ContextOperations {
    fun pref(ctx: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)!!
    fun metaData(ctx: Context): Bundle = ctx.packageManager.getApplicationInfo(ctx.packageName, PackageManager.GET_META_DATA).metaData!!
}

private class ExtendedToast private constructor(context: Context, text: String, duration: Int, isDark: Boolean) {
    private var t: Toast

    init {
        val gd = GradientDrawable().apply {
            cornerRadius = 5.dip2px().toFloat()
        }
        val txt = TextView(context).apply {
            background = gd
            setPadding(8.dip2px(), 8.dip2px(), 8.dip2px(), 8.dip2px())
            this.text = text
        }

        if (isDark) {
            gd.setColor(Color.argb(0xb0, 0x20, 0x20, 0x20))
            txt.setTextColor(Color.WHITE)
        } else {
            gd.setColor(Color.argb(0xb0, 0xcc, 0xcc, 0xcc))
            txt.setTextColor(Color.BLACK)
        }
        t = Toast(context).apply {
            this.duration = duration
            view = txt
        }
    }

    companion object {
        fun makeText(context: Context, text: String, duration: Int, isDark: Boolean = true): ExtendedToast {
            return ExtendedToast(context, text, duration, isDark)
        }
    }

    fun show() = t.show()
}