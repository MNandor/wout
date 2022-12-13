package com.mnandor.wout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_template")
data class TemplateItem(
    @PrimaryKey(autoGenerate = true)
    var itemID: Int = 0,
    val exercise: String,
    val template: String,

)
