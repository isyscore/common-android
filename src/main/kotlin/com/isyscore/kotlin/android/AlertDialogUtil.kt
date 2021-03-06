@file:Suppress("unused")

package com.isyscore.kotlin.android

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout

fun Context.alert(title: String, message: String, btn: String, callback: () -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(btn) { _, _ -> callback() }
        .show()
}

fun Context.alert(title: String, message: String, btn1: String, btn2: String, callback: (which: Int) -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(btn1) { _, _ -> callback(0) }
        .setNegativeButton(btn2) { _, _ -> callback(1) }
        .show()
}

fun Context.alert(title: String, message: String, btn1: String, btn2: String, btn3: String, callback: (which: Int) -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(btn1) { _, _ -> callback(0) }
        .setNegativeButton(btn2) { _, _ -> callback(1) }
        .setNeutralButton(btn3) { _, _ -> callback(2) }
        .show()
}

fun Context.alert(title: String, message: String, btn1: String, btn2: String, placeholder: String?, initText: String?, callback: (which: Int, text: String) -> Unit) {
    val etText = EditText(this).apply {
        hint = placeholder
        text = initText?.toEditable()
    }
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setView(etText)
        .setPositiveButton(btn1) { _, _ -> callback(0, etText.text.toString()) }
        .setNegativeButton(btn2) { _, _ -> callback(1, etText.text.toString()) }
        .show()
}

fun Context.alert(title: String, message: String, btn1: String, btn2: String, placeholder1: String?, placeholder2: String?, initText1: String?, initText2: String?, callback: (which: Int, text1: String, text2: String) -> Unit) {
    val etText1 = EditText(this).apply {
        hint = placeholder1
        text = initText1?.toEditable()
    }
    val etText2 = EditText(this).apply {
        hint = placeholder2
        text = initText2?.toEditable()
    }
    val lay = LinearLayout(this).apply {
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        orientation = LinearLayout.VERTICAL
        addView(etText1)
        addView(etText2)
    }
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setView(lay)
        .setPositiveButton(btn1) { _, _ -> callback(0, etText1.text.toString(), etText2.text.toString()) }
        .setNegativeButton(btn2) { _, _ -> callback(1, etText1.text.toString(), etText2.text.toString()) }
        .show()
}