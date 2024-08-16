package com.ristu.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.ristu.todoapp.client.TodoDatabase
import com.ristu.todoapp.constant.Constant.Companion.CREATE_TODO
import com.ristu.todoapp.databinding.ActivityAddTodoBinding
import com.ristu.todoapp.interfaces.TodoDao
import com.ristu.todoapp.model.TodoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTodoBinding
    private lateinit var todoDb: TodoDatabase
    private lateinit var todoDao: TodoDao
    private lateinit var manager: TodoManager
    private lateinit var todoModel: TodoModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = TodoManagerImpl(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_todo)
        initDb()
        todoDao = todoDb.getTodoDao()
        binding.apply {
            datePicker.month
            timePicker.hour
            onItemClick = View.OnClickListener { view ->
                when (view.id) {
                    R.id.create_todo_button -> {
                        createTodo()
                    }
                }
            }
        }
    }


    private fun createTodo() {
        lifecycleScope.launch(Dispatchers.IO) {
            binding.apply {
                todoModel = TodoModel(
                    title = titleEditText.text.toString().trim(),
                    body = bodyEditText.text.toString().trim(),
                    date = "${if (Helper.isSingleDigit(datePicker.dayOfMonth)) "0" else ""}${datePicker.dayOfMonth}-${
                        if (Helper.isSingleDigit(
                                datePicker.month + 1
                            )
                        ) "0" else ""
                    }${datePicker.month + 1}-${datePicker.year}",
                    time = "${timePicker.hour}:${timePicker.minute}"
                )
                todoDao.createTodo(
                    todoModel
                )
                val latestTodo = todoDao.getLatestTodo()
                manager.scheduleTodo(latestTodo)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddTodoActivity, "TODO created", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent().putExtra("isTodoCreated", true)
                setResult(CREATE_TODO, resultIntent)
                this@AddTodoActivity.finish()
            }
        }
    }

    private fun initDb() {
        todoDb = synchronized(this) {
            Room.databaseBuilder(
                applicationContext,
                TodoDatabase::class.java, "todo-database"
            ).build()
        }
    }
}