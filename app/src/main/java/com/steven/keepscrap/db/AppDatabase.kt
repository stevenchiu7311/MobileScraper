package com.steven.keepscrap.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Database


/**
 * Created by steven on 2019/5/13.
 */
@Database(entities = [WatchingElement::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun watchingElementDao(): WatchingElementDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getIns(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder<AppDatabase>(context.applicationContext,
                        AppDatabase::class.java, "tagfetcher.db").build()
            }
            return INSTANCE as AppDatabase
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}