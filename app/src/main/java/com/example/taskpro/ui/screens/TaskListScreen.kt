@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.taskpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskpro.data.Course
import com.example.taskpro.data.Priority
import com.example.taskpro.ui.TaskItem
import com.example.taskpro.ui.components.*
import com.example.taskpro.util.DateUtils

enum class TaskFilter(val label: String) { ALL("All"), TODO("To do"), DUE_SOON("Due soon"), OVERDUE("Overdue"), COMPLETED("Completed") }
enum class TaskSort(val label: String) { DUE_DATE("Due date"), PRIORITY("Priority"), COURSE("Course"), TITLE("Title") }

/** All tasks with filtering (status / course) and sorting (FR-09). */
@Composable
fun TaskListScreen(
    tasks: List<TaskItem>,
    courses: List<Course>,
    onOpenTask: (Int) -> Unit,
    onToggle: (TaskItem) -> Unit,
    onAddTask: () -> Unit
) {
    var filter by rememberSaveable { mutableStateOf(TaskFilter.TODO) }
    var sort by rememberSaveable { mutableStateOf(TaskSort.DUE_DATE) }
    var courseFilter by rememberSaveable { mutableStateOf<Int?>(null) }
    var sortMenu by remember { mutableStateOf(false) }

    val now = DateUtils.now()
    val visible = tasks
        .filter { courseFilter == null || it.task.courseId == courseFilter }
        .filter {
            val t = it.task
            when (filter) {
                TaskFilter.ALL -> true
                TaskFilter.TODO -> !t.isCompleted
                TaskFilter.DUE_SOON -> DateUtils.isDueSoon(t.dueDate, t.isCompleted, now)
                TaskFilter.OVERDUE -> DateUtils.isOverdue(t.dueDate, t.isCompleted, now)
                TaskFilter.COMPLETED -> t.isCompleted
            }
        }
        .let { list ->
            when (sort) {
                TaskSort.DUE_DATE -> list.sortedWith(compareBy<TaskItem>({ it.task.isCompleted }, { it.task.dueDate }))
                TaskSort.PRIORITY -> list.sortedWith(
                    compareBy<TaskItem>({ it.task.isCompleted }, { -Priority.from(it.task.priority).rank }, { it.task.dueDate })
                )
                TaskSort.COURSE -> list.sortedWith(compareBy<TaskItem>({ it.course?.code ?: "" }, { it.task.dueDate }))
                TaskSort.TITLE -> list.sortedBy { it.task.title.lowercase() }
            }
        }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (courses.isNotEmpty()) {
                FloatingActionButton(onClick = onAddTask) {
                    Icon(Icons.Filled.Add, contentDescription = "Add task")
                }
            }
        }
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner).padding(top = 20.dp)) {
            Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) { PageTitle("My Tasks", "Sorted by ${sort.label.lowercase()}") }
                Box {
                    IconButton(onClick = { sortMenu = true }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort tasks")
                    }
                    DropdownMenu(expanded = sortMenu, onDismissRequest = { sortMenu = false }) {
                        TaskSort.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = { sort = option; sortMenu = false },
                                trailingIcon = {
                                    if (option == sort) Icon(Icons.Filled.Check, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TaskFilter.entries) { option ->
                    FilterChip(
                        selected = filter == option,
                        onClick = { filter = option },
                        label = { Text(option.label) }
                    )
                }
            }
            if (courses.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = courseFilter == null,
                            onClick = { courseFilter = null },
                            label = { Text("All courses") }
                        )
                    }
                    items(courses, key = { it.id }) { course ->
                        FilterChip(
                            selected = courseFilter == course.id,
                            onClick = { courseFilter = if (courseFilter == course.id) null else course.id },
                            label = { Text(course.code) }
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            if (visible.isEmpty()) {
                EmptyState(
                    Icons.Filled.CheckCircle, "No tasks here",
                    if (courses.isEmpty()) "Add a course first, then add tasks to it."
                    else "Try another filter or add a new task."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visible, key = { it.task.id }) { item ->
                        TaskRow(item, onClick = { onOpenTask(item.task.id) }, onToggle = { onToggle(item) })
                    }
                }
            }
        }
    }
}
