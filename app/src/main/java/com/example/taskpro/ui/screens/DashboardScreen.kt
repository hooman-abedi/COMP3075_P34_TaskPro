@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.taskpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskpro.ui.CourseSummary
import com.example.taskpro.ui.TaskItem
import com.example.taskpro.ui.components.*
import com.example.taskpro.ui.theme.HighRed
import com.example.taskpro.ui.theme.MediumOrange
import com.example.taskpro.util.DateUtils
import java.time.LocalDate

/** Dashboard (FR-06, FR-12): summary cards, upcoming incomplete tasks and course progress. */
@Composable
fun DashboardScreen(
    studentName: String,
    tasks: List<TaskItem>,
    courseSummaries: List<CourseSummary>,
    onOpenTask: (Int) -> Unit,
    onToggle: (TaskItem) -> Unit,
    onSeeAllTasks: () -> Unit,
    onOpenCourses: () -> Unit,
    onOpenCourse: (Int) -> Unit,
    onAddTask: () -> Unit
) {
    val now = DateUtils.now()
    val open = tasks.filter { !it.task.isCompleted }.sortedBy { it.task.dueDate }
    val dueSoon = open.count { DateUtils.isDueSoon(it.task.dueDate, false, now) }
    val overdue = open.count { DateUtils.isOverdue(it.task.dueDate, false, now) }
    val completed = tasks.count { it.task.isCompleted }
    val upcoming = open.take(5)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (courseSummaries.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onAddTask,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Add Task") }
                )
            }
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Hello, $studentName 👋", style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold)
                        Text(DateUtils.formatLongDay(LocalDate.now()),
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(
                        Modifier.size(44.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.School, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Due soon", "$dueSoon", MediumOrange, Modifier.weight(1f), onSeeAllTasks)
                    StatCard("To do", "${open.size}", MaterialTheme.colorScheme.primary, Modifier.weight(1f), onSeeAllTasks)
                    StatCard("Overdue", "$overdue", HighRed, Modifier.weight(1f), onSeeAllTasks)
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val onPrimary = MaterialTheme.colorScheme.onPrimary
                        Text("OVERALL PROGRESS", color = onPrimary.copy(alpha = .8f),
                            style = MaterialTheme.typography.labelLarge)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("$completed / ${tasks.size}", color = onPrimary,
                                style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Text("tasks completed", color = onPrimary.copy(alpha = .85f))
                        }
                        LinearProgressIndicator(
                            progress = { if (tasks.isEmpty()) 0f else completed.toFloat() / tasks.size },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = onPrimary, trackColor = onPrimary.copy(alpha = .25f)
                        )
                    }
                }
            }

            if (courseSummaries.isEmpty()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        EmptyState(
                            icon = Icons.Filled.School,
                            title = "Start by adding your courses",
                            message = "Tasks are organized by course. Add a course, then add its assignments and quizzes.",
                            actionLabel = "Go to Courses",
                            onAction = onOpenCourses
                        )
                    }
                }
            } else {
                item { SectionHeader("Upcoming tasks", "See all", onSeeAllTasks) }
                if (upcoming.isEmpty()) {
                    item {
                        EmptyState(Icons.Filled.CheckCircle, "You're all caught up!",
                            "No unfinished tasks. Tap Add Task to plan your next one.")
                    }
                } else {
                    items(upcoming, key = { it.task.id }) { item ->
                        TaskRow(item, onClick = { onOpenTask(item.task.id) }, onToggle = { onToggle(item) })
                    }
                }

                item { SectionHeader("Progress by course", "Courses", onOpenCourses) }
                items(courseSummaries, key = { "course-${it.course.id}" }) { summary ->
                    CourseProgressRow(summary, onClick = { onOpenCourse(summary.course.id) })
                }
            }
        }
    }
}

@Composable
private fun CourseProgressRow(summary: CourseSummary, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(summary.course.code, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    "${summary.completedTasks}/${summary.total} done",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LinearProgressIndicator(
                progress = { summary.progress },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

