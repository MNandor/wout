package com.mnandor.wout

import java.text.SimpleDateFormat
import java.util.*

class DateUtility {
    companion object {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd, EEEE", Locale.getDefault())
        val compFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val pastDate = compFormat.parse("2015-06-27")



    }
}