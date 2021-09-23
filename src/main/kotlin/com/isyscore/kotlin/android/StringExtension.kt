package com.isyscore.kotlin.android

import android.text.Editable

fun String.toEditable(): Editable = Editable.Factory().newEditable(this)
