@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.taskpro.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.taskpro.data.Course
import com.example.taskpro.data.Priority
import com.example.taskpro.data.ReminderOption
import com.example.taskpro.data.Task
import com.example.taskpro.ui.components.*
import com.example.taskpro.util.DateUtils
import java.time.LocalDate
import java.time.LocalTime

/**
 * Add / Edit Task form (FR-02, FR-03, FR-04, FR-10).
 * [initialCourseId] and [initialDate] pre-fill the form when coming from a course or a calendar day.
 */
@Composable
fun TaskEditScreen(
    existing: Task?,
    courses: List<Course>,
    initialCourseId: Int?,
    initialDate: LocalDate?,
    remindersEnabled: Boolean,
    onBack: () -> Unit,
    onOpenCourses: () -> Unit,
    onSave: (id: Int?, courseId: Int, title: String, notes: String, dueDate: Long, priority: String, reminderMinutes: Int) -> Unit
) {
    if (courses.isEmpty()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            BackHeader("Add Task", onBack)
            EmptyState(
                Icons.Filled.School, "Add a course first",
                "Every task belongs to a course. Create a course, then come back to add tasks.",
                actionLabel = "Go to Courses", onAction = onOpenCourses
            )
        }
        return
    }

    val key = existing?.id ?: -1
    val existingDue = existing?.let { DateUtils.toLocalDateTime(it.dueDate) }
    var title by rememberSaveable(key) { mutableStateOf(existing?.title.orEmpty()) }
    var notes by rememberSaveable(key) { mutableStateOf(existing?.notes.orEmpty()) }
    var courseId by rememberSaveable(key) {
        mutableStateOf(existing?.courseId ?: initialCourseId?.takeIf { id -> courses.any { it.id == id } } ?: courses.first().id)
    }
    var date by rememberSaveable(key) {
        mutableStateOf(existingDue?.toLocalDate() ?: initialDate ?: LocalDate.now().plusDays(1))
    }
    var time by rememberSaveable(key) { mutableStateOf(existingDue?.toLocalTime() ?: LocalTime.of(23, 59)) }
    var priority by rememberSaveable(key) { mutableStateOf(existing?.priority ?: Priority.MEDIUM.label) }
    var reminder by rememberSaveable(key) {
        mutableStateOf(existing?.let { ReminderOption.from(it.reminderMinutes) } ?: ReminderOption.ONE_DAY)
    }
    var showErrors by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    val dueMillis = DateUtils.toMillis(date, time)
    val titleError = showErrors && title.isBlank()
    val isPast = dueMillis < DateUtils.now()

    fun save() {
        if (title.isBlank()) { showErrors = true; return }
        if (reminder != ReminderOption.NONE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        onSave(existing?.id, courseId, title, notes, dueMillis, priority, reminder.minutes)
    }

    Column(Modifier.fillMaxSize().imePadding()) {
        Box(Modifier.padding(start = 8.dp, end = 8.dp, top = 12.dp)) {
            BackHeader(if (existing == null) "Add Task" else "Edit Task", onBack)
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Task title *") },
                placeholder = { Text("e.g. Assignment 2") },
                isError = titleError,
                supportingText = { if (titleError) Text("Please enter a title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Course selector (FR-03)
            var courseMenu by remember { mutableStateOf(false) }
            val selectedCourse = courses.firstOrNull { it.id == courseId }
            ExposedDropdownMenuBox(expanded = courseMenu, onExpandedChange = { courseMenu = it }) {
                OutlinedTextField(
                    value = selectedCourse?.let { "${it.code} – ${it.name}" } ?: "Choose a course",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Course *") },
                    leadingIcon = { Icon(Icons.Filled.School, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseMenu) },
                    singleLine = true,
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = courseMenu, onDismissRequest = { courseMenu = false }) {
                    courses.forEach { course ->
                        DropdownMenuItem(
                            text = { Text("${course.code} – ${course.name}") },
                            onClick = { courseId = course.id; courseMenu = false }
                        )
                    }
                }
            }

            // Due date + time (FR-03)
            Text("Due date", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1.4f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(DateUtils.formatDate(date), maxLines = 1)
                }
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(DateUtils.formatTime(time), maxLines = 1)
                }
            }
            if (isPast) {
                Text("Heads up: this deadline is in the past.",
                    color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Priority (FR-04)
            Text("Priority", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.entries.forEach { p ->
                    FilterChip(
                        selected = priority == p.label,
                        onClick = { priority = p.label },
                        label = { Text(p.label) },
                        leadingIcon = {
                            Icon(Icons.Filled.Flag, contentDescription = null,
                                tint = priorityColor(p.label), modifier = Modifier.size(18.dp))
                        }
                    )
                }
            }

            // Reminder (FR-10)
            var reminderMenu by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = reminderMenu, onExpandedChange = { reminderMenu = it }) {
                OutlinedTextField(
                    value = reminder.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Reminder") },
                    leadingIcon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reminderMenu) },
                    supportingText = {
                        if (!remindersEnabled && reminder != ReminderOption.NONE)
                            Text("Reminders are turned off in Settings.")
                    },
                    singleLine = true,
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = reminderMenu, onDismissRequest = { reminderMenu = false }) {
                    ReminderOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = { reminder = option; reminderMenu = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes, onValueChange = { notes = it },
                label = { Text("Notes") },
                placeholder = { Text("Details, links, what to study...") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp)) { Text("Cancel") }
            Button(onClick = { save() }, modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp)) { Text("Save Task") }
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = DateUtils.localDateToPickerMillis(date))
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { date = DateUtils.pickerMillisToLocalDate(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = state)
        }
    }

    if (showTimePicker) {
        val state = rememberTimePickerState(initialHour = time.hour, initialMinute = time.minute, is24Hour = false)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Due time") },
            text = { TimePicker(state = state) },
            confirmButton = {
                TextButton(onClick = {
                    time = LocalTime.of(state.hour, state.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } }
        )
    }
}
