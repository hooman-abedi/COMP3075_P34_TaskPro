@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.taskpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.taskpro.ui.TaskItem
import com.example.taskpro.ui.components.*
import com.example.taskpro.ui.theme.HighRed
import com.example.taskpro.util.DateUtils
import java.time.LocalDate

/** Month calendar with deadline markers and the tasks of the selected day (FR-08). */
@Composable
fun CalendarScreen(
    tasks: List<TaskItem>,
    hasCourses: Boolean,
    onOpenTask: (Int) -> Unit,
    onToggle: (TaskItem) -> Unit,
    onAddTaskOn: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    var selected by rememberSaveable { mutableStateOf(today) }
    var month by rememberSaveable { mutableStateOf(today.withDayOfMonth(1)) }

    val tasksByDay = tasks.groupBy { DateUtils.toLocalDate(it.task.dueDate) }
    val dayTasks = (tasksByDay[selected] ?: emptyList())
        .sortedWith(compareBy<TaskItem>({ it.task.isCompleted }, { it.task.dueDate }))

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (hasCourses) {
                FloatingActionButton(onClick = { onAddTaskOn(selected) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add task on selected day")
                }
            }
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { PageTitle("Calendar", "Deadlines by date") }
            item {
                MonthCard(
                    month = month,
                    selected = selected,
                    today = today,
                    tasksByDay = tasksByDay,
                    onPrev = { month = month.minusMonths(1) },
                    onNext = { month = month.plusMonths(1) },
                    onToday = { month = today.withDayOfMonth(1); selected = today },
                    onSelect = { selected = it }
                )
            }
            item {
                SectionHeader(
                    if (selected == today) "Today • ${DateUtils.formatLongDay(selected)}"
                    else DateUtils.formatLongDay(selected)
                )
            }
            if (dayTasks.isEmpty()) {
                item {
                    EmptyState(Icons.Filled.EventAvailable, "Nothing due this day",
                        "Pick another date or tap + to add a deadline here.")
                }
            } else {
                items(dayTasks, key = { it.task.id }) { item ->
                    TaskRow(item, onClick = { onOpenTask(item.task.id) }, onToggle = { onToggle(item) })
                }
            }
        }
    }
}

@Composable
private fun MonthCard(
    month: LocalDate,
    selected: LocalDate,
    today: LocalDate,
    tasksByDay: Map<LocalDate, List<TaskItem>>,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    onSelect: (LocalDate) -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPrev) { Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month") }
                Text(DateUtils.formatMonth(month), Modifier.weight(1f), fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center)
                IconButton(onClick = onNext) { Icon(Icons.Filled.ChevronRight, contentDescription = "Next month") }
            }
            Row(Modifier.fillMaxWidth()) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach {
                    Text(it, Modifier.weight(1f), textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium)
                }
            }
            Spacer(Modifier.height(6.dp))

            // Monday-first grid: blank cells before the 1st, then each day of the month.
            val leadingBlanks = month.dayOfWeek.value - 1
            val cells: List<LocalDate?> = List(leadingBlanks) { null } +
                (1..month.lengthOfMonth()).map { month.withDayOfMonth(it) }
            val padded = cells + List((7 - cells.size % 7) % 7) { null }

            padded.chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        Box(Modifier.weight(1f).height(46.dp), contentAlignment = Alignment.Center) {
                            if (day != null) {
                                DayCell(
                                    day = day,
                                    isSelected = day == selected,
                                    isToday = day == today,
                                    tasks = tasksByDay[day].orEmpty(),
                                    onClick = { onSelect(day) }
                                )
                            }
                        }
                    }
                }
            }
            TextButton(onClick = onToday, modifier = Modifier.align(Alignment.End)) { Text("Today") }
        }
    }
}

@Composable
private fun DayCell(
    day: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    tasks: List<TaskItem>,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val open = tasks.filter { !it.task.isCompleted }
    val dotColor = when {
        open.any { DateUtils.isOverdue(it.task.dueDate, false) } -> HighRed
        open.isNotEmpty() -> primary
        tasks.isNotEmpty() -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> Color.Transparent
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (isSelected) primary else Color.Transparent)
                .then(if (isToday && !isSelected) Modifier.border(1.5.dp, primary, CircleShape) else Modifier)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "${day.dayOfMonth}",
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
        }
        Spacer(Modifier.height(2.dp))
        Box(Modifier.size(5.dp).clip(CircleShape).background(if (isSelected) Color.Transparent else dotColor))
    }
}
