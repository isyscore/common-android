@file:Suppress("unused")

package com.isyscore.kotlin.android

fun Int.dip2px(): Int = (this * 1f * UI.dm.density + 0.5f).toInt()
fun Int.px2dip(): Int = (this * 1f / UI.dm.density + 0.5f).toInt()
fun Int.px2scaled(): Float = (this / UI.dm.density)
fun Float.scaled2px(): Int = (this * UI.dm.density).toInt()
