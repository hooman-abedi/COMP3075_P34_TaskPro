@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.taskpro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.taskpro.data.Course
import com.example.taskpro.data.Priority
import com.example.taskpro.ui.TaskItem
import com.example.taskpro.ui.theme.HighRed
import com.example.taskpro.ui.theme.LowGreen
import com.example.taskpro.ui.theme.MediumOrange
import com.example.taskpro.util.DateUtils

fun priorityColor(priority: String): Color = when (Priority.from(priority)) {
    Priority.HIGH -> HighRed
    Priority.MEDIUM -> MediumOrange
    Priority.LOW -> LowGreen
}

@Composable
fun PageTitle(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        if (subtitle != null) {
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** Header with a back arrow used by the detail / form screens. */
@Composable
fun BackHeader(title: String, onBack: () -> Unit, actions: @Composable () -> Unit = {}) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(
            title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        actions()
    }
}

@Composable
fun Pill(text: String, color: Color) {
    Surface(color = color.copy(alpha = .14f), shape = RoundedCornerShape(30.dp)) {
        Text(
            text, color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PriorityChip(priority: String) = Pill(Priority.from(priority).label, priorityColor(priority))

/** One task row: checkbox, title, course + due date, priority. */
@Composable
fun TaskRow(
    item: TaskItem,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    showCourse: Boolean = true
) {
    val task = item.task
    val overdue = DateUtils.isOverdue(task.dueDate, task.isCompleted)
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(start = 4.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f)) {
                Text(
                    task.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                val parts = buildList {
                    if (showCourse && item.course != null) add(item.course.code)
                    add(DateUtils.relativeLabel(task.dueDate, task.isCompleted))
                }
                Text(
                    parts.joinToString("  •  "),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (overdue) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(6.dp))
            PriorityChip(task.priority)
            Icon(Icons.Filled.ChevronRight, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SectionHeader(title: String, actionLabel: String? = null, onAction: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f))
        if (actionLabel != null) TextButton(onClick = onAction) { Text(actionLabel) }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    Column(
        Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(44.dp))
        Text(title, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        if (actionLabel != null) TextButton(onClick = onAction) { Text(actionLabel) }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = { onConfirm(); onDismiss() }) {
                Text(confirmLabel, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

/** Add / edit course form shown as a dialog (FR-01). */
@Composable
fun CourseDialog(
    existing: Course?,
    onDismiss: () -> Unit,
    onSave: (code: String, name: String, instructor: String) -> Unit
) {
    var code by remember { mutableStateOf(existing?.code.orEmpty()) }
    var name by remember { mutableStateOf(existing?.name.orEmpty()) }
    var instructor by remember { mutableStateOf(existing?.instructor.orEmpty()) }
    var showErrors by remember { mutableStateOf(false) }
    val codeError = showErrors && code.isBlank()
    val nameError = showErrors && name.isBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add Course" else "Edit Course") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = code, onValueChange = { code = it.take(12) },
                    label = { Text("Course code *") }, placeholder = { Text("COMP3074") },
                    singleLine = true, isError = codeError,
                    supportingText = { if (codeError) Text("Course code is required") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Course name *") }, placeholder = { Text("Mobile Application Development") },
                    singleLine = true, isError = nameError,
                    supportingText = { if (nameError) Text("Course name is required") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = instructor, onValueChange = { instructor = it },
                    label = { Text("Instructor (optional)") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (code.isBlank() || name.isBlank()) showErrors = true
                else { onSave(code, name, instructor); onDismiss() }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
