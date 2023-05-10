package com.mnandor.wout.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
data class Exercise(
    @PrimaryKey @ColumnInfo(name = "name") val name: String,

    // without @ColumnInfo(name), the variable name is used for the column
    val usesTime: Boolean,
    val usesDistance: Boolean,
    val usesWeight: Boolean,
    val usesSetCount: Boolean,
    val usesRepCount: Boolean,

    val isDisabled: Boolean,

)
