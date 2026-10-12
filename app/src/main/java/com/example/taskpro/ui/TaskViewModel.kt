package com.example.taskpro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskpro.data.Task
import com.example.taskpro.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    val tasks = repository.tasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    fun saveTask(
        id: Int? = null,
        title: String,
        description: String,
        priority: String,
        category: String,
        dueDate: String,
        dueTime: String
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            if (id == null) {
                repository.insert(Task(
                    title = title.trim(), description = description.trim(),
                    priority = priority, category = category,
                    dueDate = dueDate, dueTime = dueTime
                ))
            } else {
                val existing = tasks.value.firstOrNull { it.id == id } ?: return@launch
                repository.update(existing.copy(
                    title = title.trim(), description = description.trim(),
                    priority = priority, category = category,
                    dueDate = dueDate, dueTime = dueTime
                ))
            }
        }
    }

    fun toggleCompleted(task: Task) {
        viewModelScope.launch { repository.update(task.copy(isCompleted = !task.isCompleted)) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.delete(task) }
    }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
