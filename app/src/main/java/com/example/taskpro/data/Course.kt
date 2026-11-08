package com.example.taskpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A course the student is taking, e.g. COMP3074 – Mobile Application Development I. */
@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,
    val name: String,
    val instructor: String? = null
)
