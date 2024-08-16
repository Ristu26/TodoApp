package com.ristu.todoapp.client

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ristu.todoapp.interfaces.TodoDao
import com.ristu.todoapp.model.TodoModel

@Database(entities = [TodoModel::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun getTodoDao(): TodoDao
}
