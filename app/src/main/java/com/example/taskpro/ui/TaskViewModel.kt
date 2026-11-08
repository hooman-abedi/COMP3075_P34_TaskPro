package com.example.taskpro.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskpro.data.Course
import com.example.taskpro.data.SampleData
import com.example.taskpro.data.SettingsStore
import com.example.taskpro.data.Task
import com.example.taskpro.data.TaskRepository
import com.example.taskpro.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** A task together with the course it belongs to, ready for the UI. */
data class TaskItem(val task: Task, val course: Course?)

/** Course with its task counts, used on the Courses screen and statistics. */
data class CourseSummary(val course: Course, val openTasks: Int, val completedTasks: Int) {
    val total: Int get() = openTasks + completedTasks
    val progress: Float get() = if (total == 0) 0f else completedTasks.toFloat() / total
}

class TaskViewModel(
    private val repository: TaskRepository,
    private val settings: SettingsStore,
    private val appContext: Context
) : ViewModel() {

    val courses: StateFlow<List<Course>> =
        repository.courses.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val tasks: StateFlow<List<TaskItem>> =
        combine(repository.tasks, repository.courses) { tasks, courses ->
            val byId = courses.associateBy { it.id }
            tasks.map { TaskItem(it, byId[it.courseId]) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val courseSummaries: StateFlow<List<CourseSummary>> =
        combine(repository.courses, repository.tasks) { courses, tasks ->
            courses.map { course ->
                val forCourse = tasks.filter { it.courseId == course.id }
                CourseSummary(
                    course = course,
                    openTasks = forCourse.count { !it.isCompleted },
                    completedTasks = forCourse.count { it.isCompleted }
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val studentName: StateFlow<String> = settings.studentName
    val darkTheme: StateFlow<Boolean> = settings.darkTheme
    val remindersEnabled: StateFlow<Boolean> = settings.remindersEnabled

    // ---------- Courses (FR-01) ----------

    fun saveCourse(id: Int?, code: String, name: String, instructor: String) {
        if (code.isBlank() || name.isBlank()) return
        viewModelScope.launch {
            repository.saveCourse(
                Course(
                    id = id ?: 0,
                    code = code.trim().uppercase(),
                    name = name.trim(),
                    instructor = instructor.trim().ifBlank { null }
                )
            )
            // Course code is shown in reminders, so refresh them.
            if (id != null) rescheduleCourse(id)
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.tasksForCourse(course.id).forEach { ReminderScheduler.cancel(appContext, it.id) }
            repository.deleteCourse(course)
        }
    }

    // ---------- Tasks (FR-02 – FR-05) ----------

    fun saveTask(
        id: Int?,
        courseId: Int,
        title: String,
        notes: String,
        dueDate: Long,
        priority: String,
        reminderMinutes: Int
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val existing = tasks.value.firstOrNull { it.task.id == id }?.task
            val task = Task(
                id = id ?: 0,
                courseId = courseId,
                title = title.trim(),
                notes = notes.trim(),
                dueDate = dueDate,
                priority = priority,
                isCompleted = existing?.isCompleted ?: false,
                reminderMinutes = reminderMinutes
            )
            val savedId = repository.saveTask(task)
            schedule(task.copy(id = savedId))
        }
    }

    fun toggleCompleted(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            repository.saveTask(updated)
            schedule(updated)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            ReminderScheduler.cancel(appContext, task.id)
            repository.deleteTask(task)
        }
    }

    // ---------- Settings ----------

    fun setStudentName(name: String) = settings.setStudentName(name)
    fun setDarkTheme(enabled: Boolean) = settings.setDarkTheme(enabled)

    fun setRemindersEnabled(enabled: Boolean) {
        settings.setRemindersEnabled(enabled)
        viewModelScope.launch { rescheduleAll() }
    }

    fun loadSampleData() {
        viewModelScope.launch {
            SampleData.build().forEach { sample ->
                val courseId = repository.saveCourse(sample.course)
                sample.tasks.forEach { t ->
                    val task = Task(
                        courseId = courseId,
                        title = t.title,
                        notes = t.notes,
                        dueDate = SampleData.dueMillis(t),
                        priority = t.priority.label,
                        isCompleted = t.completed,
                        reminderMinutes = if (t.completed) Task.NO_REMINDER else 60
                    )
                    val newId = repository.saveTask(task)
                    ReminderScheduler.schedule(appContext, task.copy(id = newId), sample.course.code)
                }
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.allTasks().forEach { ReminderScheduler.cancel(appContext, it.id) }
            repository.clearAll()
        }
    }

    // ---------- Reminder helpers ----------

    private suspend fun schedule(task: Task) {
        val code = repository.allCourses().firstOrNull { it.id == task.courseId }?.code
        ReminderScheduler.schedule(appContext, task, code)
    }

    private suspend fun rescheduleCourse(courseId: Int) {
        repository.tasksForCourse(courseId).forEach { schedule(it) }
    }

    private suspend fun rescheduleAll() {
        val codes = repository.allCourses().associate { it.id to it.code }
        repository.allTasks().forEach { task ->
            if (settings.remindersEnabled.value) {
                ReminderScheduler.schedule(appContext, task, codes[task.courseId])
            } else {
                ReminderScheduler.cancel(appContext, task.id)
            }
        }
    }
}

class TaskViewModelFactory(
    private val repository: TaskRepository,
    private val settings: SettingsStore,
    private val appContext: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(repository, settings, appContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
