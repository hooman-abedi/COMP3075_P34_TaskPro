package com.example.taskpro.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY code COLLATE NOCASE ASC")
    fun observeCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses")
    suspend fun getAll(): List<Course>

    @Insert
    suspend fun insert(course: Course): Long

    @Update
    suspend fun update(course: Course)

    @Delete
    suspend fun delete(course: Course)

    @Query("DELETE FROM courses")
    suspend fun deleteAll()
}
