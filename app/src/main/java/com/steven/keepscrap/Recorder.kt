package com.steven.keepscrap

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import java.io.FileWriter
import java.io.IOException


/**
 * Created by steven on 2019/5/16.
 */
object Recorder {
    fun save(context: Context, data: String) {
        try {
            val path = ContextWrapper(context).getFilesDir().absolutePath + "/record.txt"
            val outputStreamWriter = FileWriter(path, true)
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }
}