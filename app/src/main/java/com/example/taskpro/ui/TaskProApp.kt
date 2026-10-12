package com.example.taskpro.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.taskpro.data.Task

private val Blue = Color(0xFF1769E8)
private val Pale = Color(0xFFEAF2FF)
private val Muted = Color(0xFF6F7D91)

private sealed class Screen(val route: String, val label: String) {
    data object Home : Screen("home", "Home")
    data object Calendar : Screen("calendar", "Calendar")
    data object Tasks : Screen("tasks", "Tasks")
    data object Settings : Screen("settings", "Settings")
    data object Add : Screen("add?taskId={taskId}", "Add")
    data object Detail : Screen("detail/{taskId}", "Details")
}

@Composable
fun TaskProApp(viewModel: TaskViewModel) {
    val navController = rememberNavController()
    val tasks by viewModel.tasks.collectAsState()
    val bottomRoutes = listOf(Screen.Home, Screen.Calendar, Screen.Tasks, Screen.Settings)

    Scaffold(
        bottomBar = {
            val entry by navController.currentBackStackEntryAsState()
            val route = entry?.destination?.route
            if (route in bottomRoutes.map { it.route }) {
                NavigationBar(containerColor = Color.White) {
                    bottomRoutes.forEach { screen ->
                        val selected = route == screen.route
                        val icon = when (screen) {
                            Screen.Home -> Icons.Default.Home
                            Screen.Calendar -> Icons.Default.CalendarMonth
                            Screen.Tasks -> Icons.Default.CheckCircle
                            else -> Icons.Default.Settings
                        }
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            } },
                            icon = { Icon(icon, contentDescription = screen.label) },
                            label = { Text(screen.label) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            val entry by navController.currentBackStackEntryAsState()
            if (entry?.destination?.route in listOf("home", "tasks", "calendar")) {
                FloatingActionButton(
                    onClick = { navController.navigate("add") },
                    containerColor = Blue,
                    contentColor = Color.White
                ) { Icon(Icons.Default.Add, contentDescription = "Add task") }
            }
        },
        containerColor = Color(0xFFF6F8FC)
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(tasks, onOpenTasks = { navController.navigate("tasks") },
                    onOpenTask = { navController.navigate("detail/${it.id}") },
                    onToggle = viewModel::toggleCompleted)
            }
            composable(Screen.Tasks.route) {
                TaskListScreen(tasks, onOpenTask = { navController.navigate("detail/${it.id}") },
                    onToggle = viewModel::toggleCompleted)
            }
            composable(Screen.Calendar.route) { CalendarScreen(tasks) }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(
                route = "add?taskId={taskId}",
                arguments = listOf(navArgument("taskId") {
                    type = NavType.StringType; nullable = true; defaultValue = null
                })
            ) { backStack ->
                val taskId = backStack.arguments?.getString("taskId")?.toIntOrNull()
                val existing = tasks.firstOrNull { it.id == taskId }
                AddTaskScreen(
                    existing = existing,
                    onBack = { navController.popBackStack() },
                    onSave = { id, title, desc, priority, category, date, time ->
                        viewModel.saveTask(id, title, desc, priority, category, date, time)
                        navController.popBackStack()
                    }
                )
            }
            composable("detail/{taskId}", arguments = listOf(navArgument("taskId") {
                type = NavType.IntType
            })) { backStack ->
                val task = tasks.firstOrNull { it.id == backStack.arguments?.getInt("taskId") }
                if (task != null) DetailScreen(
                    task = task,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate("add?taskId=${task.id}") },
                    onToggle = { viewModel.toggleCompleted(task) },
                    onDelete = { viewModel.deleteTask(task); navController.popBackStack() }
                ) else EmptyState("Task not found", "Return to the task list.")
            }
        }
    }
}

@Composable
private fun PageTitle(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        if (subtitle != null) Text(subtitle, color = Muted, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun HomeScreen(
    tasks: List<Task>, onOpenTasks: () -> Unit, onOpenTask: (Task) -> Unit,
    onToggle: (Task) -> Unit
) {
    val completed = tasks.count { it.isCompleted }
    val active = tasks.filterNot { it.isCompleted }.take(4)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Hello, Winthrop 👋", style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
                    Text("Here's your day at a glance", color = Muted)
                }
                Box(Modifier.size(44.dp).background(Pale, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Blue)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Blue)) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("TODAY'S TASKS", color = Color.White.copy(alpha = .8f), style = MaterialTheme.typography.labelLarge)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("$completed / ${tasks.size}", color = Color.White,
                            style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text("completed", color = Color.White.copy(alpha = .85f))
                    }
                    LinearProgressIndicator(
                        progress = { if (tasks.isEmpty()) 0f else completed.toFloat() / tasks.size },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color.White, trackColor = Color.White.copy(alpha = .25f)
                    )
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Upcoming reminders", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f))
                TextButton(onClick = onOpenTasks) { Text("See all") }
            }
        }
        if (active.isEmpty()) {
            item { EmptyState("You're all caught up!", "Tap + to add your first task.") }
        } else {
            items(active, key = { it.id }) { task ->
                TaskCard(task, onClick = { onOpenTask(task) }, onToggle = { onToggle(task) })
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = Blue, modifier = Modifier.size(30.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Small steps add up", fontWeight = FontWeight.SemiBold)
                        Text("Pick one task and make progress today.", color = Muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(task: Task, onClick: () -> Unit, onToggle: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f)) {
                Text(task.title, fontWeight = FontWeight.SemiBold,
                    color = if (task.isCompleted) Muted else Color(0xFF152238))
                Text(
                    listOf(task.category, task.dueTime).filter { it.isNotBlank() }.joinToString(" • ").ifBlank { "No schedule" },
                    color = Muted, style = MaterialTheme.typography.bodySmall
                )
            }
            PriorityChip(task.priority)
            Icon(Icons.Default.ChevronRight, contentDescription = "Open task", tint = Muted)
        }
    }
}

@Composable
private fun PriorityChip(priority: String) {
    val color = when (priority.lowercase()) {
        "high" -> Color(0xFFD83D4D)
        "low" -> Color(0xFF179B70)
        else -> Color(0xFFED9A20)
    }
    Surface(color = color.copy(alpha = .12f), shape = RoundedCornerShape(30.dp)) {
        Text(priority, color = color, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TaskListScreen(
    tasks: List<Task>, onOpenTask: (Task) -> Unit, onToggle: (Task) -> Unit
) {
    var filter by remember { mutableStateOf("All") }
    val filtered = when (filter) {
        "Today" -> tasks.filter { it.dueDate.equals("Today", true) }
        "Upcoming" -> tasks.filterNot { it.isCompleted }
        "Completed" -> tasks.filter { it.isCompleted }
        else -> tasks
    }
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 18.dp)) {
        PageTitle("My Tasks", "Keep everything in one place")
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Today", "Upcoming", "Completed").forEach { option ->
                FilterChip(selected = filter == option, onClick = { filter = option }, label = { Text(option) })
            }
        }
        Spacer(Modifier.height(8.dp))
        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState("No tasks here", "Add a task using the + button.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                items(filtered, key = { it.id }) { task ->
                    TaskCard(task, onClick = { onOpenTask(task) }, onToggle = { onToggle(task) })
                }
            }
        }
    }
}

@Composable
private fun CalendarScreen(tasks: List<Task>) {
    var selectedDay by remember { mutableIntStateOf(11) }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PageTitle("Calendar", "Your schedule, at a glance")
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { selectedDay = (selectedDay - 1).coerceAtLeast(1) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous day")
                    }
                    Text("October 2026", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    IconButton(onClick = { selectedDay = (selectedDay + 1).coerceAtMost(31) }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next day")
                    }
                }
                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    days.forEach { Text(it, color = Muted, modifier = Modifier.width(32.dp)) }
                }
                Spacer(Modifier.height(10.dp))
                (1..35).chunked(7).forEach { week ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        week.forEach { day ->
                            if (day <= 31) {
                                Surface(
                                    onClick = { selectedDay = day },
                                    shape = CircleShape,
                                    color = if (day == selectedDay) Blue else Color.Transparent,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("$day", color = if (day == selectedDay) Color.White else Color(0xFF26364B))
                                    }
                                }
                            } else Spacer(Modifier.size(32.dp))
                        }
                    }
                }
            }
        }
        Text("Tasks for October $selectedDay", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        val dayTasks = tasks.filter { it.dueDate.contains("$selectedDay") || it.dueDate.equals("Today", true) }
        if (dayTasks.isEmpty()) EmptyState("Nothing scheduled", "Your free time is a chance to get ahead.")
        else dayTasks.forEach { TaskCard(it, onClick = {}, onToggle = {}) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskScreen(
    existing: Task?,
    onBack: () -> Unit,
    onSave: (Int?, String, String, String, String, String, String) -> Unit
) {
    var title by remember(existing?.id) { mutableStateOf(existing?.title.orEmpty()) }
    var description by remember(existing?.id) { mutableStateOf(existing?.description.orEmpty()) }
    var priority by remember(existing?.id) { mutableStateOf(existing?.priority ?: "Medium") }
    var category by remember(existing?.id) { mutableStateOf(existing?.category ?: "Study") }
    var date by remember(existing?.id) { mutableStateOf(existing?.dueDate ?: "Oct 11, 2026") }
    var time by remember(existing?.id) { mutableStateOf(existing?.dueTime ?: "10:00 AM") }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text(if (existing == null) "Add Task" else "Edit Task",
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task title *") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = description, onValueChange = { description = it },
                label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Text("Priority", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Low", "Medium", "High").forEach {
                    FilterChip(selected = priority == it, onClick = { priority = it }, label = { Text(it) })
                }
            }
            Text("Category", fontWeight = FontWeight.SemiBold)
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(value = category, onValueChange = {}, readOnly = true,
                    label = { Text("Choose category") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Study", "Work", "Personal", "Health", "Other").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { category = it; expanded = false })
                    }
                }
            }
            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Due date") },
                modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.CalendarMonth, null) })
            OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Due time") },
                modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Schedule, null) })
            Text("Enter date and time as text (e.g. Oct 11, 2026 / 10:00 AM).", color = Muted,
                style = MaterialTheme.typography.bodySmall)
        }
        Button(
            onClick = { onSave(existing?.id, title, description, priority, category, date, time) },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = title.isNotBlank(),
            shape = RoundedCornerShape(16.dp)
        ) { Text("Save Task") }
    }
}

@Composable
private fun DetailScreen(
    task: Task, onBack: () -> Unit, onEdit: () -> Unit,
    onToggle: () -> Unit, onDelete: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("Task Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f))
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit task") }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                PriorityChip(task.priority)
                Text(task.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Blue)
                    Spacer(Modifier.width(10.dp))
                    Text("${task.dueDate.ifBlank { "No due date" }}  •  ${task.dueTime.ifBlank { "No time" }}")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Category, contentDescription = null, tint = Blue)
                    Spacer(Modifier.width(10.dp))
                    Text(task.category)
                }
                Text("Description", fontWeight = FontWeight.Bold)
                Text(task.description.ifBlank { "No description added." }, color = Muted)
            }
        }
        Button(onClick = onToggle, modifier = Modifier.fillMaxWidth()) {
            Text(if (task.isCompleted) "Mark as Incomplete" else "Mark as Completed")
        }
        OutlinedButton(onClick = onDelete, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD83D4D))) {
            Text("Delete Task")
        }
    }
}

@Composable
private fun SettingsScreen() {
    var notifications by remember { mutableStateOf(true) }
    var reminders by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        PageTitle("Settings", "Make TaskPro work for you")
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp)) {
            Column {
                SettingRow(Icons.Default.Notifications, "Notifications", "Task alerts and updates", notifications) { notifications = it }
                HorizontalDivider()
                SettingRow(Icons.Default.Alarm, "Due reminders", "Remind me about upcoming tasks", reminders) { reminders = it }
                HorizontalDivider()
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = Blue)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Appearance", fontWeight = FontWeight.SemiBold)
                        Text("Light theme", color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Pale), shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.padding(18.dp)) {
                Text("TaskPro", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("COMP3074 Final Project • Version 1.0", color = Muted)
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Blue)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}

@Composable
private fun EmptyState(title: String, message: String) {
    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Blue, modifier = Modifier.size(42.dp))
        Text(title, fontWeight = FontWeight.Bold)
        Text(message, color = Muted)
    }
}
