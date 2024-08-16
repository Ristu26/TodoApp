package com.ristu.todoapp

import com.ristu.todoapp.model.TodoModel

interface TodoManager {
    fun scheduleTodo(item: TodoModel)
    fun cancelTodo(uid: Long)
}