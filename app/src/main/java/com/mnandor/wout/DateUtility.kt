package com.mnandor.wout

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class DateUtility {
    companion object {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd, EEEE", Locale.getDefault())
        val compFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val pastDate = compFormat.parse("2015-06-27")
        val completionTimestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        public fun getToday() = compFormat.format(Date())

        fun offsetModifier(totalDays: Int): Long {
            val today = Date()
            val old = DateUtility.pastDate

            val diff: Long = today.getTime() - old.getTime()
            val days: Long = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

            return days%totalDays
        }

        fun validateCompletionDate(input: String):Boolean{
            var dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            completionTimestampFormat.isLenient = false
            return try {
                completionTimestampFormat.parse(input)
                LocalDateTime.parse(input, dtf)
                true
            } catch (e: Exception) {
                false
            }
        }

        fun parseFormatToUnix(input: String): Int {
            return (completionTimestampFormat.parse(input).time/1000).toInt()
        }

        fun getDateOnlyFromFull(input: String): String {
            val fullDate = completionTimestampFormat.parse(input)
            return compFormat.format(fullDate)
        }


    }
}