package com.example.taskpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val priority: String = "Medium",
    val category: String = "Personal",
    val dueDate: String = "",
    val dueTime: String = "",
    val isCompleted: Boolean = false
)
