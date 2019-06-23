package com.steven.keepscrap.db

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Created by steven on 2019/5/13.
 */

@Entity(tableName = "elements", indices = [Index(value = ["url", "path"], unique = true)])
data class WatchingElement(
        @NonNull @ColumnInfo(name = "url") var url: String,
        @NonNull @ColumnInfo(name = "path") var path: String) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    override fun toString(): String = "WatchingElement(id:$id url: $url path:$path)"
}