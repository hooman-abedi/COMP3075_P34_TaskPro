package com.example.taskpro.data

import com.example.taskpro.util.DateUtils
import java.time.LocalDate
import java.time.LocalTime

/** Demo content so the app can be shown quickly (Settings -> Load sample data). */
object SampleData {
    data class SampleTask(
        val title: String,
        val daysFromToday: Long,
        val time: LocalTime,
        val priority: Priority,
        val notes: String = "",
        val completed: Boolean = false
    )

    data class SampleCourse(val course: Course, val tasks: List<SampleTask>)

    fun build(): List<SampleCourse> = listOf(
        SampleCourse(
            Course(code = "COMP3074", name = "Mobile Application Development I", instructor = "Prof. Smith"),
            listOf(
                SampleTask("Functional prototype", 1, LocalTime.of(23, 59), Priority.HIGH,
                    "Submit GitHub link and demo video on D2L."),
                SampleTask("Lab exercise 8", 4, LocalTime.of(17, 0), Priority.LOW),
                SampleTask("Final project presentation", 21, LocalTime.of(10, 0), Priority.HIGH),
                SampleTask("Lab exercise 7", -2, LocalTime.of(17, 0), Priority.LOW, completed = true)
            )
        ),
        SampleCourse(
            Course(code = "COMP3123", name = "Full Stack Development I", instructor = "Prof. Lee"),
            listOf(
                SampleTask("Assignment 2 – REST API", 3, LocalTime.of(23, 59), Priority.HIGH,
                    "Express + MongoDB, include Postman screenshots."),
                SampleTask("Quiz 3", 6, LocalTime.of(9, 0), Priority.MEDIUM)
            )
        ),
        SampleCourse(
            Course(code = "COMP2138", name = "Database Systems", instructor = null),
            listOf(
                SampleTask("Database quiz", 2, LocalTime.of(11, 0), Priority.MEDIUM, "Chapters 6–8: joins and normalization."),
                SampleTask("Read chapter 9", -1, LocalTime.of(20, 0), Priority.LOW),
                SampleTask("ER diagram homework", -4, LocalTime.of(23, 59), Priority.MEDIUM, completed = true)
            )
        )
    )

    fun dueMillis(task: SampleTask): Long =
        DateUtils.toMillis(LocalDate.now().plusDays(task.daysFromToday), task.time)
}
