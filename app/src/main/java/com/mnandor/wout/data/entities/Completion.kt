package com.mnandor.wout.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completion")
data class Completion(
    @PrimaryKey @ColumnInfo(name = "timestamp") val timestamp: String,

    val exercise: String,

    val duration: String?,
    val distance: Float?,
    val weight: Float?,
    val sets: Int?,
    val reps: Int?,

)
