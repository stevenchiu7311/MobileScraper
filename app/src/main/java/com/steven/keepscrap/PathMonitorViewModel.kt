package com.steven.keepscrap

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.steven.keepscrap.db.AppDatabase
import com.steven.keepscrap.db.DatabaseInitializer
import com.steven.keepscrap.db.WatchingElement


/**
 * Created by steven on 2019/5/12.
 */
class PathMonitorViewModel : ViewModel() {
    private lateinit var mDb: AppDatabase
    private lateinit var mElementsResult: LiveData<List<WatchingElement>>

    fun getAll(): LiveData<List<WatchingElement>>{
        return mElementsResult
    }

    fun subscribeToDbChanges() {
        val elements = mDb.watchingElementDao().findAllElements()
        mElementsResult = Transformations.map<List<WatchingElement>, List<WatchingElement>>(elements,
                object : androidx.arch.core.util.Function<List<WatchingElement>, List<WatchingElement>> {
                    override fun apply(items: List<WatchingElement>): List<WatchingElement> {
                        return items
                    }
                })
    }
    fun createDb(context: Context) {
        mDb = AppDatabase.getIns(context)
        // Populate it with initial data
        AsyncTask.execute {
            DatabaseInitializer.populateAsync(mDb)
        }
    }

    fun add(url: String, path: String): WatchingElement {
        val element = WatchingElement(url, path)
        mDb.watchingElementDao().insertElement(element)
        return element
    }

    fun remove(url: String, path: String): WatchingElement {
        val element = WatchingElement(url, path)
        mDb.watchingElementDao().deleteElement(url, path)
        return element
    }

    fun clear(url: String) {
        mDb.watchingElementDao().deleteElementByUrl(url)
    }
}