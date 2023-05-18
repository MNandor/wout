package com.mnandor.wout

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateUtility {
    companion object {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd, EEEE", Locale.getDefault())
        val compFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val pastDate = compFormat.parse("2015-06-27")
        public fun getToday() = compFormat.format(Date())

        fun offsetModifier(totalDays: Int): Long {
            val today = Date()
            val old = DateUtility.pastDate

            val diff: Long = today.getTime() - old.getTime()
            val days: Long = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

            return days%totalDays
        }



    }
}