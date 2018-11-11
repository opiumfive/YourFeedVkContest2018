package com.opiumfive.yourfeed.utils


import android.content.Context
import com.opiumfive.yourfeed.R
import java.text.SimpleDateFormat
import java.util.*


class DateFormatter(private val context: Context) {

    private val timeFormat = "'${context.getString(R.string.at)}' HH:mm"
    private val russianDateFormat = "d MMMM $timeFormat"
    private val englishDateFormat = "MMMM d '${context.getString(R.string.at)}' $timeFormat"
    private val russianLocale = Locale("ru", "RU")
    private val today = Calendar.getInstance()

    private val onlyTimeFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
    private val dateTimeFormat = if (Locale.getDefault() == russianLocale) {
        SimpleDateFormat(russianDateFormat, Locale.getDefault())
    } else {
        SimpleDateFormat(englishDateFormat, Locale.getDefault())
    }

    fun format(date: Long): String {
        try {
            val timestampLong = date * 1000
            val calendar = Calendar.getInstance().apply {
                time = Date(timestampLong)
            }
            return getLocalDateTimeString(calendar)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    private fun getLocalDateTimeString(c: Calendar) = when {
        today.get(Calendar.DATE) == c.get(Calendar.DATE) -> "${context.getString(R.string.today)} ${onlyTimeFormat.format(
            c.time
        )}"
        today.get(Calendar.DATE) - c.get(Calendar.DATE) == 1 -> "${context.getString(R.string.yesterday)} ${onlyTimeFormat.format(
            c.time
        )}"
        else -> dateTimeFormat.format(c.time)
    }
}