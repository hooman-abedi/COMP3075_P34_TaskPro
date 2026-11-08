@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.taskpro.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskpro.ui.screens.CalendarScreen
import com.example.taskpro.ui.screens.CourseDetailScreen
import com.example.taskpro.ui.screens.CoursesScreen
import com.example.taskpro.ui.screens.DashboardScreen
import com.example.taskpro.ui.screens.SettingsScreen
import com.example.taskpro.ui.screens.TaskDetailScreen
import com.example.taskpro.ui.screens.TaskEditScreen
import com.example.taskpro.ui.screens.TaskListScreen
import java.time.LocalDate

/** Routes used by Navigation Compose. */
object Routes {
    const val HOME = "home"
    const val COURSES = "courses"
    const val CALENDAR = "calendar"
    const val TASKS = "tasks"
    const val SETTINGS = "settings"
    const val COURSE_DETAIL = "course/{courseId}"
    const val TASK_DETAIL = "task/{taskId}"
    const val TASK_EDIT = "editTask?taskId={taskId}&courseId={courseId}&date={date}"

    fun courseDetail(id: Int) = "course/$id"
    fun taskDetail(id: Int) = "task/$id"
    fun addTask(courseId: Int? = null, date: LocalDate? = null) =
        "editTask?courseId=${courseId ?: -1}&date=${date?.toEpochDay() ?: Long.MIN_VALUE}"
    fun editTask(taskId: Int) = "editTask?taskId=$taskId"
}

private data class BottomItem(val route: String, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "Home", Icons.Filled.Home),
    BottomItem(Routes.COURSES, "Courses", Icons.AutoMirrored.Filled.MenuBook),
    BottomItem(Routes.CALENDAR, "Calendar", Icons.Filled.CalendarMonth),
    BottomItem(Routes.TASKS, "Tasks", Icons.Filled.Checklist),
    BottomItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings)
)

@Composable
fun TaskProApp(viewModel: TaskViewModel) {
    val navController = rememberNavController()
    val tasks by viewModel.tasks.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val summaries by viewModel.courseSummaries.collectAsState()
    val studentName by viewModel.studentName.collectAsState()
    val darkTheme by viewModel.darkTheme.collectAsState()
    val remindersEnabled by viewModel.remindersEnabled.collectAsState()

    val entry by navController.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (bottomItems.any { it.route == currentRoute }) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = { navController.navigateTopLevel(item.route) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                DashboardScreen(
                    studentName = studentName,
                    tasks = tasks,
                    courseSummaries = summaries,
                    onOpenTask = { navController.navigate(Routes.taskDetail(it)) },
                    onToggle = { viewModel.toggleCompleted(it.task) },
                    onSeeAllTasks = { navController.navigateTopLevel(Routes.TASKS) },
                    onOpenCourses = { navController.navigateTopLevel(Routes.COURSES) },
                    onOpenCourse = { navController.navigate(Routes.courseDetail(it)) },
                    onAddTask = { navController.navigate(Routes.addTask()) }
                )
            }

            composable(Routes.COURSES) {
                CoursesScreen(
                    summaries = summaries,
                    onOpenCourse = { navController.navigate(Routes.courseDetail(it)) },
                    onSaveCourse = viewModel::saveCourse,
                    onDeleteCourse = viewModel::deleteCourse
                )
            }

            composable(
                Routes.COURSE_DETAIL,
                arguments = listOf(navArgument("courseId") { type = NavType.IntType })
            ) { backStack ->
                val courseId = backStack.arguments?.getInt("courseId") ?: -1
                CourseDetailScreen(
                    summary = summaries.firstOrNull { it.course.id == courseId },
                    tasks = tasks.filter { it.task.courseId == courseId },
                    onBack = { navController.popBackStack() },
                    onOpenTask = { navController.navigate(Routes.taskDetail(it)) },
                    onToggle = { viewModel.toggleCompleted(it.task) },
                    onAddTask = { navController.navigate(Routes.addTask(courseId = it)) },
                    onSaveCourse = viewModel::saveCourse,
                    onDeleteCourse = viewModel::deleteCourse
                )
            }

            composable(Routes.CALENDAR) {
                CalendarScreen(
                    tasks = tasks,
                    hasCourses = courses.isNotEmpty(),
                    onOpenTask = { navController.navigate(Routes.taskDetail(it)) },
                    onToggle = { viewModel.toggleCompleted(it.task) },
                    onAddTaskOn = { navController.navigate(Routes.addTask(date = it)) }
                )
            }

            composable(Routes.TASKS) {
                TaskListScreen(
                    tasks = tasks,
                    courses = courses,
                    onOpenTask = { navController.navigate(Routes.taskDetail(it)) },
                    onToggle = { viewModel.toggleCompleted(it.task) },
                    onAddTask = { navController.navigate(Routes.addTask()) }
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    studentName = studentName,
                    darkTheme = darkTheme,
                    remindersEnabled = remindersEnabled,
                    onNameChange = viewModel::setStudentName,
                    onDarkThemeChange = viewModel::setDarkTheme,
                    onRemindersChange = viewModel::setRemindersEnabled,
                    onLoadSampleData = viewModel::loadSampleData,
                    onClearData = viewModel::clearAllData
                )
            }

            composable(
                Routes.TASK_DETAIL,
                arguments = listOf(navArgument("taskId") { type = NavType.IntType })
            ) { backStack ->
                val taskId = backStack.arguments?.getInt("taskId") ?: -1
                TaskDetailScreen(
                    item = tasks.firstOrNull { it.task.id == taskId },
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Routes.editTask(it)) },
                    onOpenCourse = { navController.navigate(Routes.courseDetail(it)) },
                    onToggle = { viewModel.toggleCompleted(it.task) },
                    onDelete = {
                        viewModel.deleteTask(it.task)
                        navController.popBackStack()
                    }
                )
            }

            composable(
                Routes.TASK_EDIT,
                arguments = listOf(
                    navArgument("taskId") { type = NavType.IntType; defaultValue = -1 },
                    navArgument("courseId") { type = NavType.IntType; defaultValue = -1 },
                    navArgument("date") { type = NavType.LongType; defaultValue = Long.MIN_VALUE }
                )
            ) { backStack ->
                val args = backStack.arguments
                val taskId = args?.getInt("taskId") ?: -1
                val courseId = args?.getInt("courseId") ?: -1
                val epochDay = args?.getLong("date") ?: Long.MIN_VALUE
                TaskEditScreen(
                    existing = tasks.firstOrNull { it.task.id == taskId }?.task,
                    courses = courses,
                    initialCourseId = courseId.takeIf { it >= 0 },
                    initialDate = epochDay.takeIf { it != Long.MIN_VALUE }?.let { LocalDate.ofEpochDay(it) },
                    remindersEnabled = remindersEnabled,
                    onBack = { navController.popBackStack() },
                    onOpenCourses = { navController.navigateTopLevel(Routes.COURSES) },
                    onSave = { id, cId, title, notes, due, priority, reminder ->
                        viewModel.saveTask(id, cId, title, notes, due, priority, reminder)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/** Switch between bottom-navigation tabs without stacking copies of them. */
private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
