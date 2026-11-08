package com.example.taskpro.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * An academic task (assignment, quiz, project, study task...).
 * Each task belongs to exactly one [Course] (one course -> many tasks).
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("courseId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: Int,
    val title: String,
    val notes: String = "",
    /** Deadline as epoch milliseconds (date + time). */
    val dueDate: Long,
    /** "Low", "Medium" or "High" – see [Priority]. */
    val priority: String = Priority.MEDIUM.label,
    val isCompleted: Boolean = false,
    /** Minutes before the deadline to show a reminder; [NO_REMINDER] = off. */
    val reminderMinutes: Int = NO_REMINDER
) {
    companion object {
        const val NO_REMINDER = -1
    }
}

enum class Priority(val label: String, val rank: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3);

    companion object {
        fun from(value: String): Priority =
            entries.firstOrNull { it.label.equals(value, ignoreCase = true) } ?: MEDIUM
    }
}

/** Reminder choices offered in the Add / Edit Task screen. */
enum class ReminderOption(val minutes: Int, val label: String) {
    NONE(Task.NO_REMINDER, "No reminder"),
    AT_TIME(0, "At due time"),
    ONE_HOUR(60, "1 hour before"),
    ONE_DAY(24 * 60, "1 day before"),
    TWO_DAYS(2 * 24 * 60, "2 days before");

    companion object {
        fun from(minutes: Int): ReminderOption =
            entries.firstOrNull { it.minutes == minutes } ?: NONE
    }
}
