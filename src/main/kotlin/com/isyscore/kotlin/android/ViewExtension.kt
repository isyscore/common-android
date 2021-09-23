@file:Suppress("unused")

package com.isyscore.kotlin.android

import android.view.View

fun <T : View> View.v(resId: Int): T = findViewById(resId)
