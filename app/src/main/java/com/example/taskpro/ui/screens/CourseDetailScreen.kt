@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.taskpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskpro.data.Course
import com.example.taskpro.ui.CourseSummary
import com.example.taskpro.ui.TaskItem
import com.example.taskpro.ui.components.*

/** Course details: course info plus all tasks for that course (FR-07). */
@Composable
fun CourseDetailScreen(
    summary: CourseSummary?,
    tasks: List<TaskItem>,
    onBack: () -> Unit,
    onOpenTask: (Int) -> Unit,
    onToggle: (TaskItem) -> Unit,
    onAddTask: (courseId: Int) -> Unit,
    onSaveCourse: (id: Int?, code: String, name: String, instructor: String) -> Unit,
    onDeleteCourse: (Course) -> Unit
) {
    if (summary == null) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            BackHeader("Course", onBack)
            EmptyState(Icons.Filled.School, "Course not found", "It may have been deleted.")
        }
        return
    }
    val course = summary.course
    var showEdit by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }
    val open = tasks.filter { !it.task.isCompleted }.sortedBy { it.task.dueDate }
    val done = tasks.filter { it.task.isCompleted }.sortedByDescending { it.task.dueDate }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onAddTask(course.id) },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Add Task") }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                BackHeader(course.code, onBack) {
                    IconButton(onClick = { showEdit = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit course")
                    }
                    IconButton(onClick = { showDelete = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete course",
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(course.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(
                            "Instructor: ${course.instructor ?: "Not set"}",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { summary.progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp)
                        )
                        Text(
                            "${summary.completedTasks} of ${summary.total} task(s) completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            item { SectionHeader("To do (${open.size})") }
            if (open.isEmpty()) {
                item {
                    EmptyState(Icons.Filled.CheckCircle, "Nothing to do here",
                        "Add an assignment, quiz or project for ${course.code}.")
                }
            }
            items(open, key = { it.task.id }) { item ->
                TaskRow(item, onClick = { onOpenTask(item.task.id) }, onToggle = { onToggle(item) },
                    showCourse = false)
            }

            if (done.isNotEmpty()) {
                item { SectionHeader("Completed (${done.size})") }
                items(done, key = { it.task.id }) { item ->
                    TaskRow(item, onClick = { onOpenTask(item.task.id) }, onToggle = { onToggle(item) },
                        showCourse = false)
                }
            }
        }
    }

    if (showEdit) {
        CourseDialog(
            existing = course,
            onDismiss = { showEdit = false },
            onSave = { code, name, instructor -> onSaveCourse(course.id, code, name, instructor) }
        )
    }
    if (showDelete) {
        ConfirmDialog(
            title = "Delete ${course.code}?",
            message = "This will also delete the ${summary.total} task(s) in this course.",
            confirmLabel = "Delete",
            onConfirm = { onDeleteCourse(course); onBack() },
            onDismiss = { showDelete = false }
        )
    }
}
