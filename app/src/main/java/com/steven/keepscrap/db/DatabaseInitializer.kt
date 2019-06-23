package com.steven.keepscrap.db

import android.os.AsyncTask

/**
 * Created by steven on 2019/5/13.
 */
object DatabaseInitializer {
    val TAG = DatabaseInitializer.javaClass.simpleName

    fun populateAsync(db: AppDatabase) {
        val task = PopulateDbAsync(db)
        task.execute()
    }

    private fun populateWithTestData(db: AppDatabase) {
        //db.watchingElementDao().deleteAll()

        // val element = add(db, "1")
    }

    private class PopulateDbAsync internal constructor(private val mDb: AppDatabase) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void): Void? {
            populateWithTestData(mDb)
            return null
        }
    }
}