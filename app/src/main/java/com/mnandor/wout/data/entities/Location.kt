package com.mnandor.wout.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "location", indices = [Index(value = ["exercise", "location"], unique = true)])
data class Location(
    @PrimaryKey(autoGenerate = true)
    var itemID: Int = 0,
    @ColumnInfo(name="exercise")
    val exercise: String,
    @ColumnInfo(name="location")
    val template: String,

)
