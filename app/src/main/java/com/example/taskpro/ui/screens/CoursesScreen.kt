@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.taskpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.taskpro.data.Course
import com.example.taskpro.ui.CourseSummary
import com.example.taskpro.ui.components.*

/** Courses list with add / edit / delete (FR-01). */
@Composable
fun CoursesScreen(
    summaries: List<CourseSummary>,
    onOpenCourse: (Int) -> Unit,
    onSaveCourse: (id: Int?, code: String, name: String, instructor: String) -> Unit,
    onDeleteCourse: (Course) -> Unit
) {
    var editing by remember { mutableStateOf<Course?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<CourseSummary?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { editing = null; showDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Add Course") }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { PageTitle("Courses", "${summaries.size} course(s) this term") }
            if (summaries.isEmpty()) {
                item {
                    EmptyState(
                        Icons.Filled.School, "No courses yet",
                        "Add the courses you are taking this term (e.g. COMP3074).",
                        actionLabel = "Add a course", onAction = { editing = null; showDialog = true }
                    )
                }
            }
            items(summaries, key = { it.course.id }) { summary ->
                CourseCard(
                    summary = summary,
                    onClick = { onOpenCourse(summary.course.id) },
                    onEdit = { editing = summary.course; showDialog = true },
                    onDelete = { deleting = summary }
                )
            }
        }
    }

    if (showDialog) {
        val course = editing
        CourseDialog(
            existing = course,
            onDismiss = { showDialog = false },
            onSave = { code, name, instructor -> onSaveCourse(course?.id, code, name, instructor) }
        )
    }

    deleting?.let { summary ->
        ConfirmDialog(
            title = "Delete ${summary.course.code}?",
            message = "This will also delete the ${summary.total} task(s) in this course. This cannot be undone.",
            confirmLabel = "Delete",
            onConfirm = { onDeleteCourse(summary.course) },
            onDismiss = { deleting = null }
        )
    }
}

@Composable
private fun CourseCard(
    summary: CourseSummary,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).background(
                    MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(summary.course.code, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium)
                Text(summary.course.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                val instructor = summary.course.instructor
                Text(
                    buildString {
                        append("${summary.openTasks} open task(s)")
                        if (summary.completedTasks > 0) append(" • ${summary.completedTasks} done")
                        if (!instructor.isNullOrBlank()) append(" • $instructor")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit ${summary.course.code}")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete ${summary.course.code}",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
