package com.example.taskpro

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskpro.data.SettingsStore
import com.example.taskpro.data.TaskDatabase
import com.example.taskpro.data.TaskRepository
import com.example.taskpro.notifications.ReminderScheduler
import com.example.taskpro.ui.TaskProApp
import com.example.taskpro.ui.TaskViewModel
import com.example.taskpro.ui.TaskViewModelFactory
import com.example.taskpro.ui.theme.TaskProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReminderScheduler.createChannel(this)

        val repository = TaskRepository(TaskDatabase.getDatabase(applicationContext))
        val settings = SettingsStore(applicationContext)

        setContent {
            val vm: TaskViewModel = viewModel(
                factory = TaskViewModelFactory(repository, settings, applicationContext)
            )
            val dark by vm.darkTheme.collectAsState()

            // Keep status/navigation bar icons readable for the selected theme.
            DisposableEffect(dark) {
                val style = if (dark) SystemBarStyle.dark(Color.TRANSPARENT)
                else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
                onDispose { }
            }

            TaskProTheme(darkTheme = dark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskProApp(viewModel = vm)
                }
            }
        }
    }
}
