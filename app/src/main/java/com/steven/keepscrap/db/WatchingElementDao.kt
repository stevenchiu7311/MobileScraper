package com.steven.keepscrap.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE


@Dao
interface WatchingElementDao {

    @Query("select * from elements where id = :id")
    fun loadElementById(id: Int): WatchingElement


    @Query("SELECT * FROM elements")
    fun findAllElements(): LiveData<List<WatchingElement>>


    @Query("SELECT * FROM elements")
    fun findAllElementSync(): List<WatchingElement>

    @Insert(onConflict = IGNORE)
    fun insertElement(element: WatchingElement)

    @Update(onConflict = REPLACE)
    fun updateElement(element: WatchingElement)

    @Query("DELETE FROM elements where url = :url AND path = :path")
    fun deleteElement(url: String, path: String)

    @Query("DELETE FROM elements where url = :url")
    fun deleteElementByUrl(url: String)

    @Query("DELETE FROM elements")
    fun deleteAll()
}