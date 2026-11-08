package com.example.taskpro.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    /** Incomplete tasks first, then by closest deadline. */
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC")
    fun observeTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks")
    suspend fun getAll(): List<Task>

    @Query("SELECT * FROM tasks WHERE courseId = :courseId")
    suspend fun getForCourse(courseId: Int): List<Task>

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM tasks WHERE courseId = :courseId")
    suspend fun deleteForCourse(courseId: Int)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}
