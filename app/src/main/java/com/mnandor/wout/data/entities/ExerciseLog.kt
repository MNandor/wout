package com.mnandor.wout.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_log")
data class ExerciseLog(
    @PrimaryKey @ColumnInfo(name = "timestamp") val timestamp: String,

    val exercise: String,

    val duration: String?,
    val distance: Float?,
    val weight: Float?,
    val sets: Int?,
    val reps: Int?,

)
