package com.ristu.todoapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.ristu.todoapp.client.TodoDatabase
import com.ristu.todoapp.constant.Constant
import com.ristu.todoapp.databinding.ActivityTodoViewBinding
import com.ristu.todoapp.interfaces.TodoDao
import com.ristu.todoapp.model.TodoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class TodoViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoViewBinding
    private var uid: Long? = null
    private lateinit var todoDb: TodoDatabase
    private lateinit var todoDao: TodoDao
    private lateinit var todo: TodoModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_view)
        uid = intent.getLongExtra("UID", 0)
        initDb()
        todoDao = todoDb.getTodoDao()
        getTodo()
        binding.apply {
            onItemClick = View.OnClickListener { view ->
                when (view.id) {
                    R.id.save_button -> {
                        updateTodo()
                    }
                }
            }
        }
    }

    private fun updateTodo() {
        lifecycleScope.launch(Dispatchers.IO) {
            binding.apply {
                todoDao.updateDateTimeTodo(
                    time = "${timePicker.hour}:${timePicker.minute}",
                    date = "${if (Helper.isSingleDigit(datePicker.dayOfMonth)) "0" else ""}${datePicker.dayOfMonth}-${if (Helper.isSingleDigit(datePicker.month + 1)) "0" else ""}${datePicker.month + 1}-${datePicker.year}",
                    uid = uid!!
                )
                todoDao.updateTodo(
                    title = titleEditText.text.toString(),
                    body = bodyEditText.text.toString(),
                    uid = uid!!
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TodoViewActivity, "TODO Updated", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent().putExtra("isTodoUpdated", true)
                    setResult(Constant.UPDATE_TODO, resultIntent)
                    this@TodoViewActivity.finish()
                    getTodo()
                }
            }
        }
    }

    private fun getTodo() {
        lifecycleScope.launch(Dispatchers.IO) {
            todo = todoDao.getTodo(uid!!)
            withContext(Dispatchers.Main) {
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val date = LocalDate.parse(todo.date, formatter)
                val year = date.year
                val month = date.monthValue - 1
                val day = date.dayOfMonth

                val timeCalendar = Helper.extractHourAndMinute(todo.time!!)
                val hour = timeCalendar.get(Calendar.HOUR_OF_DAY)
                val minute = timeCalendar.get(Calendar.MINUTE)

                binding.apply {
                    timePicker.hour = hour
                    timePicker.minute = minute
                    datePicker.updateDate(year, month, day)
                    titleEditText.setText(todo.title.toString())
                    bodyEditText.setText(todo.body.toString())
                    dateTextView.text = todo.date
                    timeTextView.text = todo.time
                }
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