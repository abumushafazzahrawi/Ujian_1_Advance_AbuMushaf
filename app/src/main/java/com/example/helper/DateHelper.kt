package com.example.helper

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateHelper {

    fun getCurrentDate(dateString: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = dateFormat.parse(dateString)

            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

            if (date != null) {
                outputFormat.format(date)
            } else {
                "-"
            }
        } catch (e: Exception) {
            dateString
        }
    }
}