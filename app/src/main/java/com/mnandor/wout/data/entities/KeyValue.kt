package com.mnandor.wout.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "kvpairs")
data class KeyValue(
    @PrimaryKey @ColumnInfo(name = "key") val key: String,
    val value: String
)