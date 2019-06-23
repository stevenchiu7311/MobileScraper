package com.steven.keepscrap

/**
 * Created by steven on 2019/5/7.
 */

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

internal class JavaScriptInterface(context: Context, cb: Callback) {
    var mCb: Callback? = null
    var mContext: Context

    companion object {
        var TAG: String = this.javaClass.simpleName
    }

    init {
        mCb = cb
        mContext = context
    }

    @JavascriptInterface
    fun getFileContents(path: String): String? {
        return readAssetsContent(mContext, path)
    }

    @JavascriptInterface
    fun onElementClick(str: String) {
        Log.i(TAG, str)
        if (mCb != null) {
            mCb!!.onElementClick(str)
        }
    }

    // Read resources from "assets" folder in string
    fun readAssetsContent(context: Context, name: String): String? {
        var `in`: BufferedReader? = null
        try {
            val buf = StringBuilder()
            val `is` = context.assets.open(name)
            `in` = BufferedReader(InputStreamReader(`is`))

            var str: String?
            var isFirst = true
            do {
                str = `in`.readLine()
                if (str == null) {
                    break
                }
                if (isFirst)
                    isFirst = false
                else
                    buf.append('\n')
                buf.append(str)
            } while (true)
            return buf.toString()
        } catch (e: IOException) {
            Log.e("error", "Error opening asset " + name)
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    Log.e("error", "Error closing asset " + name)
                }

            }
        }

        return null
    }

    interface Callback {
        fun onElementClick(path: String)
    }
}
