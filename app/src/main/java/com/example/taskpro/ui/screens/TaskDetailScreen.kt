@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.taskpro.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskpro.data.ReminderOption
import com.example.taskpro.ui.TaskItem
import com.example.taskpro.ui.components.*
import com.example.taskpro.ui.theme.LowGreen
import com.example.taskpro.ui.theme.MediumOrange
import com.example.taskpro.util.DateUtils

/** Task details with complete / edit / delete controls (FR-02, FR-05). */
@Composable
fun TaskDetailScreen(
    item: TaskItem?,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    onOpenCourse: (Int) -> Unit,
    onToggle: (TaskItem) -> Unit,
    onDelete: (TaskItem) -> Unit
) {
    if (item == null) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            BackHeader("Task Details", onBack)
            EmptyState(Icons.Filled.CheckCircle, "Task not found", "It may have been deleted.")
        }
        return
    }
    val task = item.task
    var confirmDelete by remember { mutableStateOf(false) }
    val overdue = DateUtils.isOverdue(task.dueDate, task.isCompleted)

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BackHeader("Task Details", onBack) {
            IconButton(onClick = { onEdit(task.id) }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit task")
            }
        }
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PriorityChip(task.priority)
                    when {
                        task.isCompleted -> Pill("Completed", LowGreen)
                        overdue -> Pill("Overdue", MaterialTheme.colorScheme.error)
                        DateUtils.isDueSoon(task.dueDate, false) -> Pill("Due soon", MediumOrange)
                        else -> Pill("Upcoming", MaterialTheme.colorScheme.primary)
                    }
                }
                Text(task.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                DetailRow(
                    Icons.Filled.School, "Course",
                    item.course?.let { "${it.code} – ${it.name}" } ?: "Unknown course",
                    onClick = item.course?.let { c -> { onOpenCourse(c.id) } }
                )
                DetailRow(Icons.Filled.CalendarMonth, "Due", DateUtils.formatDateTime(task.dueDate))
                DetailRow(Icons.Filled.Schedule, "Time left", DateUtils.relativeLabel(task.dueDate, task.isCompleted))
                DetailRow(Icons.Filled.Notifications, "Reminder", ReminderOption.from(task.reminderMinutes).label)

                HorizontalDivider()
                Text("Notes", fontWeight = FontWeight.Bold)
                Text(task.notes.ifBlank { "No notes added." },
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Button(
            onClick = { onToggle(item) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(if (task.isCompleted) Icons.AutoMirrored.Filled.Undo else Icons.Filled.CheckCircle, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (task.isCompleted) "Mark as Incomplete" else "Mark as Completed")
        }
        OutlinedButton(
            onClick = { confirmDelete = true },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Delete Task")
        }
    }

    if (confirmDelete) {
        ConfirmDialog(
            title = "Delete task?",
            message = "\"${task.title}\" will be permanently deleted.",
            confirmLabel = "Delete",
            onConfirm = { onDelete(item) },
            onDismiss = { confirmDelete = false }
        )
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String, onClick: (() -> Unit)? = null) {
    val clickable = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(clickable.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value)
        }
        if (onClick != null) {
            Icon(Icons.Filled.ChevronRight, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
