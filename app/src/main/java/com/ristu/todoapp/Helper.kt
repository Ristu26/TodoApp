package com.ristu.todoapp

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

class Helper {
    companion object {
        fun isSingleDigit(number: Int): Boolean {
            return number in 0..9
        }

        @SuppressLint("SimpleDateFormat")
        fun extractHourAndMinute(timeString: String): Calendar {
            val formatter = SimpleDateFormat("HH:mm")
            val date = formatter.parse(timeString)
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }
            return calendar
        }

        fun convertToTimestamp(date: String, time: String): Long {
            val dateComponents = date.split("-")
            val timeComponents = time.split(":")
            if (dateComponents.size != 3) {
                throw IllegalArgumentException("Invalid date format. Expected DD-MM-YYYY")
            }
            if (timeComponents.size != 2) {
                throw IllegalArgumentException("Invalid time format. Expected HH:MM")
            }

            val day = dateComponents[0].toInt()
            val month = dateComponents[1].toInt()
            val year = dateComponents[2].toInt()
            val hour = timeComponents[0].toInt()
            val minute = timeComponents[1].toInt()

            val dateTime = LocalDateTime.of(year, month, day, hour, minute, 0)
            return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        }
    }
}