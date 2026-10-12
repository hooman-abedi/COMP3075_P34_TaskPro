package com.example.taskpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskpro.data.TaskDatabase
import com.example.taskpro.data.TaskRepository
import com.example.taskpro.ui.TaskProApp
import com.example.taskpro.ui.TaskViewModel
import com.example.taskpro.ui.TaskViewModelFactory
import com.example.taskpro.ui.theme.TaskProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = TaskDatabase.getDatabase(applicationContext)
        val repository = TaskRepository(database.taskDao())
        setContent {
            TaskProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val vm: TaskViewModel = viewModel(factory = TaskViewModelFactory(repository))
                    TaskProApp(viewModel = vm)
                }
            }
        }
    }
}
