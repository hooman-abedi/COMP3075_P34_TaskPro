package com.example.taskpro.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.taskpro.data.SettingsStore
import com.example.taskpro.data.Task
import com.example.taskpro.util.DateUtils

/**
 * Schedules local deadline reminders (FR-10) with AlarmManager.
 * Inexact alarms are used so no special "exact alarm" permission is required.
 */
object ReminderScheduler {
    const val CHANNEL_ID = "task_reminders"
    const val EXTRA_TASK_ID = "extra_task_id"
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_TEXT = "extra_text"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID, "Deadline reminders", NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Reminders before task deadlines" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun schedule(context: Context, task: Task, courseCode: String?) {
        cancel(context, task.id)
        if (task.isCompleted || task.reminderMinutes < 0) return
        if (!SettingsStore.remindersEnabled(context)) return
        val triggerAt = task.dueDate - task.reminderMinutes * 60_000L
        if (triggerAt <= System.currentTimeMillis()) return

        val title = if (courseCode.isNullOrBlank()) task.title else "$courseCode: ${task.title}"
        val text = "Due ${DateUtils.relativeLabel(task.dueDate)}"
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_TASK_ID, task.id)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_TEXT, text)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, task.id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    fun cancel(context: Context, taskId: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, taskId, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return
        context.getSystemService(AlarmManager::class.java).cancel(pendingIntent)
        pendingIntent.cancel()
    }
}
