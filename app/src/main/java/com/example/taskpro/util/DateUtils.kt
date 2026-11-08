package com.example.taskpro.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** All dates are stored as epoch milliseconds in the database; these helpers convert/format them. */
object DateUtils {
    private const val DAY_MS = 24L * 60 * 60 * 1000
    const val DUE_SOON_DAYS = 3

    private val zone: ZoneId get() = ZoneId.systemDefault()
    private val fullDate = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.getDefault())
    private val shortDate = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
    private val time = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
    private val monthYear = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    private val longDay = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())

    fun now(): Long = System.currentTimeMillis()

    fun toLocalDateTime(millis: Long): LocalDateTime =
        Instant.ofEpochMilli(millis).atZone(zone).toLocalDateTime()

    fun toLocalDate(millis: Long): LocalDate = toLocalDateTime(millis).toLocalDate()

    fun toMillis(date: LocalDate, time: LocalTime): Long =
        LocalDateTime.of(date, time).atZone(zone).toInstant().toEpochMilli()

    /** Material3 DatePicker works with UTC midnight millis. */
    fun pickerMillisToLocalDate(millis: Long): LocalDate =
        Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()

    fun localDateToPickerMillis(date: LocalDate): Long =
        date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    fun formatDate(millis: Long): String = toLocalDateTime(millis).format(fullDate)
    fun formatShortDate(millis: Long): String = toLocalDateTime(millis).format(shortDate)
    fun formatTime(millis: Long): String = toLocalDateTime(millis).format(time)
    fun formatDateTime(millis: Long): String = "${formatDate(millis)} • ${formatTime(millis)}"
    fun formatDate(date: LocalDate): String = date.format(fullDate)
    fun formatTime(t: LocalTime): String = t.format(time)
    fun formatMonth(date: LocalDate): String = date.format(monthYear)
    fun formatLongDay(date: LocalDate): String = date.format(longDay)

    fun isOverdue(dueMillis: Long, completed: Boolean, now: Long = System.currentTimeMillis()): Boolean =
        !completed && dueMillis < now

    fun isDueSoon(dueMillis: Long, completed: Boolean, now: Long = System.currentTimeMillis()): Boolean =
        !completed && dueMillis >= now && dueMillis - now <= DUE_SOON_DAYS * DAY_MS

    /** "Overdue", "Today, 5:00 PM", "Tomorrow, 9:00 AM", "In 4 days" or "Nov 20". */
    fun relativeLabel(dueMillis: Long, completed: Boolean = false): String {
        val due = toLocalDateTime(dueMillis)
        val days = ChronoUnit.DAYS.between(LocalDate.now(), due.toLocalDate())
        val timeText = due.toLocalTime().format(time)
        return when {
            isOverdue(dueMillis, completed) -> "Overdue • ${formatShortDate(dueMillis)}"
            days == 0L -> "Today, $timeText"
            days == 1L -> "Tomorrow, $timeText"
            days in 2L..6L -> "In $days days • ${formatShortDate(dueMillis)}"
            else -> "${formatShortDate(dueMillis)}, $timeText"
        }
    }
}
