package com.mnandor.wout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "day_template", indices = [Index(value = ["exercise", "template"], unique = true)])
data class TemplateItem(
    @PrimaryKey(autoGenerate = true)
    var itemID: Int = 0,
    @ColumnInfo(name="exercise")
    val exercise: String,
    @ColumnInfo(name="template")
    val template: String,

)
