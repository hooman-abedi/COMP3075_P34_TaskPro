package com.example.taskpro.data

import kotlinx.coroutines.flow.Flow

/** Single access point to the local Room database for courses and tasks. */
class TaskRepository(database: TaskDatabase) {
    private val courseDao = database.courseDao()
    private val taskDao = database.taskDao()

    val courses: Flow<List<Course>> = courseDao.observeCourses()
    val tasks: Flow<List<Task>> = taskDao.observeTasks()

    /** Inserts or updates a course and returns its id. */
    suspend fun saveCourse(course: Course): Int =
        if (course.id == 0) courseDao.insert(course).toInt()
        else { courseDao.update(course); course.id }

    /** Deletes the course together with all of its tasks. */
    suspend fun deleteCourse(course: Course) {
        taskDao.deleteForCourse(course.id)
        courseDao.delete(course)
    }

    /** Inserts or updates a task and returns its id. */
    suspend fun saveTask(task: Task): Int =
        if (task.id == 0) taskDao.insert(task).toInt()
        else { taskDao.update(task); task.id }

    suspend fun deleteTask(task: Task) = taskDao.delete(task)

    suspend fun tasksForCourse(courseId: Int): List<Task> = taskDao.getForCourse(courseId)
    suspend fun allTasks(): List<Task> = taskDao.getAll()
    suspend fun allCourses(): List<Course> = courseDao.getAll()

    suspend fun clearAll() {
        taskDao.deleteAll()
        courseDao.deleteAll()
    }
}
