package com.example.taskpro.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.taskpro.data.TaskDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Alarms are cleared when the phone restarts, so re-schedule pending reminders after boot. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = TaskDatabase.getDatabase(appContext)
                val courses = db.courseDao().getAll().associateBy { it.id }
                db.taskDao().getAll().forEach { task ->
                    ReminderScheduler.schedule(appContext, task, courses[task.courseId]?.code)
                }
            } finally {
                pending.finish()
            }
        }
    }
}
