@file:Suppress("DEPRECATION", "unused")

package com.isyscore.kotlin.android

import android.content.AsyncTaskLoader
import android.content.Context
import android.database.Cursor

abstract class BaseClassLoader<T>(context: Context) : AsyncTaskLoader<T>(context) {
    abstract override fun loadInBackground(): T
    override fun onStartLoading() = forceLoad()
    override fun onStopLoading() {
        cancelLoad()
    }

    override fun onReset() = stopLoading()
}

abstract class BaseListLoader<T>(context: Context) : BaseClassLoader<List<T>>(context) {
    abstract override fun loadInBackground(): List<T>
}

abstract class BaseMutableListLoader<T>(context: Context) : BaseClassLoader<MutableList<T>>(context) {
    abstract override fun loadInBackground(): MutableList<T>
}

abstract class BaseCursorLoader(context: Context) : BaseClassLoader<Cursor>(context) {
    abstract override fun loadInBackground(): Cursor
}

