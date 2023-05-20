package com.mnandor.wout.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduleday")
data class ScheduleDay(
    @PrimaryKey @ColumnInfo(name = "day")
    val day: Int,

    @ColumnInfo(name="location")
    val location: Int,

    @ColumnInfo(name="exercises")
    val exercises: String,
)
