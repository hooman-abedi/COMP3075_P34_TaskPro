package com.example.taskpro.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Small user preferences (name, dark theme, reminders) saved with SharedPreferences. */
class SettingsStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _studentName = MutableStateFlow(prefs.getString(KEY_NAME, "Student") ?: "Student")
    val studentName: StateFlow<String> = _studentName.asStateFlow()

    private val _darkTheme = MutableStateFlow(prefs.getBoolean(KEY_DARK, false))
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    private val _remindersEnabled = MutableStateFlow(prefs.getBoolean(KEY_REMINDERS, true))
    val remindersEnabled: StateFlow<Boolean> = _remindersEnabled.asStateFlow()

    fun setStudentName(name: String) {
        prefs.edit().putString(KEY_NAME, name).apply()
        _studentName.value = name
    }

    fun setDarkTheme(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK, enabled).apply()
        _darkTheme.value = enabled
    }

    fun setRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REMINDERS, enabled).apply()
        _remindersEnabled.value = enabled
    }

    companion object {
        private const val PREFS_NAME = "taskpro_settings"
        private const val KEY_NAME = "student_name"
        private const val KEY_DARK = "dark_theme"
        private const val KEY_REMINDERS = "reminders_enabled"

        /** Used by broadcast receivers that run without the UI. */
        fun remindersEnabled(context: Context): Boolean =
            context.applicationContext
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_REMINDERS, true)
    }
}
