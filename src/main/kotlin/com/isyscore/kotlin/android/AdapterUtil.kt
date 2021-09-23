@file:Suppress("DEPRECATION", "MemberVisibilityCanBePrivate", "unused")

package com.isyscore.kotlin.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import com.isyscore.kotlin.common.DownloadState
import com.isyscore.kotlin.common.download
import java.io.File
import kotlin.concurrent.thread


/**
 * T: Base Type in Adapter
 * H: Holder Class
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseAdapter<T, H>(ctx: Context, protected var list: MutableList<T>) : BaseAdapter(), Filterable {

    protected val lock = Any()
    private var inflater: LayoutInflater = LayoutInflater.from(ctx)
    protected var listFull = list
    private var _filter = ArrayFilter()
    protected val context = ctx

    fun resStr(resId: Int): String = context.resStr(resId)
    fun resStr(resId: Int, vararg args: Any?) = context.resStr(resId, *args)
    fun resStrArray(resId: Int) = context.resStrArray(resId)
    fun resColor(resId: Int) = context.resColor(resId)
    fun resDrawable(resId: Int) = context.resDrawable(resId)

    open fun setNewList(list: MutableList<T>) {
        this.listFull = list
        this.list = list
        notifyDataSetChanged()
    }

    open fun deleteItem(item: T) {
        list.remove(item)
        listFull.remove(item)
        notifyDataSetChanged()
    }

    open fun deleteItems(items: MutableList<T>) {
        list.minusAssign(items)
        listFull.minusAssign(items)
        notifyDataSetChanged()
    }

    fun showOrDownloadImage(iv: ImageView, imgFile: String, downloadUrl: String, loadingImage: Bitmap? = null, failImage: Bitmap? = null) = showOrDownloadImage(iv, File(imgFile), downloadUrl, loadingImage, failImage)
    fun showOrDownloadImage(iv: ImageView, imgFile: File, downloadUrl: String, loadingImage: Bitmap? = null, failImage: Bitmap? = null) {
        if (imgFile.exists()) {
            iv.setImageBitmap(BitmapFactory.decodeStream(imgFile.inputStream()))
        } else {
            if (loadingImage != null) {
                iv.setImageBitmap(loadingImage)
            }
            if (!imgFile.parentFile.exists()) {
                imgFile.parentFile.mkdirs()
            }
            thread {
                download {
                    url = downloadUrl
                    localFile = imgFile.absolutePath
                    progress { state, _, _, _ ->
                        if (state == DownloadState.WHAT_DOWNLOAD_FINISH) {
                            runOnMainThread {
                                if (imgFile.exists()) {
                                    iv.setImageBitmap(BitmapFactory.decodeStream(imgFile.inputStream()))
                                } else {
                                    if (failImage != null) {
                                        iv.setImageBitmap(failImage)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getItem(position: Int): Any? = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list.size

    abstract fun getValueText(item: T): String?

    abstract fun getAdapterLayout(): Int

    abstract fun newHolder(baseView: View): H

    abstract fun fillHolder(baseVew: View, holder: H, item: T, position: Int)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = convertView
        if (v == null) {
            v = inflater.inflate(getAdapterLayout(), parent, false)
        }
        var holder = v!!.tag as H?
        if (holder == null) {
            holder = newHolder(v)
            v.tag = holder
        }
        val item = list[position]
        fillHolder(v, holder!!, item, position)
        return v
    }

    open fun filter(text: String?) {
        _filter.filter(text)
    }

    override fun getFilter() = _filter

    inner class ArrayFilter : Filter() {
        override fun performFiltering(prefix: CharSequence?): FilterResults {
            list = listFull
            val results = FilterResults()
            if (prefix == null || prefix.isEmpty()) {
                synchronized(lock) {
                    val l = list
                    results.values = l
                    results.count = l.size
                }
            } else {
                val prefixString = prefix.toString().toLowerCase()
                val values = list
                val count = values.size
                val newValues = arrayListOf<T>()

                for (i in 0 until count) {
                    val value = values[i]
                    val valueText = getValueText(value)
                    if (valueText?.indexOf(prefixString) != -1) {
                        newValues.add(value)
                    }
                }
                results.values = newValues
                results.count = newValues.size
            }
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results?.values != null) {
                list = results.values as MutableList<T>
                if (results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }

    }
}

