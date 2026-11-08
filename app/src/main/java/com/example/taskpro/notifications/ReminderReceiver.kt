package com.example.taskpro.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.taskpro.MainActivity
import com.example.taskpro.data.SettingsStore

/** Shows the reminder notification when the alarm fires. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!SettingsStore.remindersEnabled(context)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val taskId = intent.getIntExtra(ReminderScheduler.EXTRA_TASK_ID, 0)
        val title = intent.getStringExtra(ReminderScheduler.EXTRA_TITLE) ?: "Task reminder"
        val text = intent.getStringExtra(ReminderScheduler.EXTRA_TEXT) ?: "A task is due soon"

        val openApp = PendingIntent.getActivity(
            context, taskId,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, ReminderScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(taskId, notification)
        } catch (e: SecurityException) {
            // Notification permission was revoked – nothing to show.
        }
    }
}
