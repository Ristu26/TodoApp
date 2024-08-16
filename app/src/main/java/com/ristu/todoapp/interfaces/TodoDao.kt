package com.ristu.todoapp.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ristu.todoapp.model.TodoModel

@Dao
interface TodoDao {
    @Query("SELECT * FROM all_todo")
    fun getAllTodo(): List<TodoModel>

    @Query("SELECT * FROM all_todo WHERE uid=:uid")
    fun getTodo(uid: Long): TodoModel

    @Query("UPDATE all_todo SET title =:title, body =:body WHERE uid=:uid")
    fun updateTodo(title: String, body: String, uid: Long)

    @Query("UPDATE all_todo SET time =:time, date =:date WHERE uid=:uid")
    fun updateDateTimeTodo(time: String, date: String, uid: Long)

    @Query("SELECT * FROM all_todo ORDER BY uid DESC LIMIT 1")
    fun getLatestTodo(): TodoModel

    @Insert
    fun createTodo(user: TodoModel)

    @Query("DELETE FROM all_todo WHERE uid = :uid")
    fun deleteTodo(uid: Long)
}