package com.mnandor.wout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_template")
data class ExerciseTemplate(
    @PrimaryKey @ColumnInfo(name = "name") val name: String,

    // without @ColumnInfo(name), the variable name is used for the column
    val usesTime: Boolean,
    val usesDistance: Boolean,
    val usesWeight: Boolean,
    val usesSetCount: Boolean,
    val usesRepCount: Boolean,

    val isDisabled: Boolean,

)
