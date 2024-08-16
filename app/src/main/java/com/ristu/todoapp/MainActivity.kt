package com.ristu.todoapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room.databaseBuilder
import com.ristu.todoapp.adapter.TodoRecyclerAdapter
import com.ristu.todoapp.client.TodoDatabase
import com.ristu.todoapp.constant.Constant
import com.ristu.todoapp.constant.Constant.Companion.CREATE_TODO
import com.ristu.todoapp.constant.Constant.Companion.UPDATE_TODO
import com.ristu.todoapp.databinding.ActivityMainBinding
import com.ristu.todoapp.interfaces.TodoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TodoRecyclerAdapter
    private lateinit var todoDb: TodoDatabase
    private lateinit var todoDao: TodoDao
    private lateinit var manager: TodoManager

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = TodoManagerImpl(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        createNotificationChannel()
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 206)
            }
        }
        initValues()
        initDb()
        todoDao = todoDb.getTodoDao()
        getAllTodo()
        binding.apply {
            onItemClick = View.OnClickListener { view ->
                when (view.id) {
                    R.id.add_todo_button -> {
                        startActivityForResult(
                            Intent(
                                this@MainActivity,
                                AddTodoActivity::class.java
                            ), 999
                        )
                    }
                }
            }
        }
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.onActivityResult(requestCode, resultCode, data)",
            "androidx.appcompat.app.AppCompatActivity"
        )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == CREATE_TODO && data?.getBooleanExtra("isTodoCreated", false) == true ||
            resultCode == UPDATE_TODO && data?.getBooleanExtra("isTodoUpdated", false) == true
        ) {
            getAllTodo()
        }
    }

    private fun getAllTodo() {
        lifecycleScope.launch(Dispatchers.IO) {
            val allTodo = todoDao.getAllTodo()
            withContext(Dispatchers.Main) {
                if (allTodo.isNotEmpty()) {
                    binding.isEmptyTodo = false
                    adapter.setTodoData(allTodo)
                } else {
                    binding.isEmptyTodo = true
                }
            }
        }
    }

    private fun initValues() {
        adapter = TodoRecyclerAdapter({ uid ->
            val intent = Intent(this@MainActivity, TodoViewActivity::class.java)
            intent.putExtra("UID", uid)
            startActivityForResult(Intent(intent), 111)
        }, { uid ->
            lifecycleScope.launch(Dispatchers.IO) {
                todoDao.deleteTodo(uid)
                manager.cancelTodo(uid)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "TODO Deleted", Toast.LENGTH_SHORT).show()
                    getAllTodo()
                }
            }
        })
        binding.apply {
            todoRecyclerView.adapter = adapter
            todoRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun initDb() {
        todoDb = synchronized(this) {
            databaseBuilder(
                applicationContext,
                TodoDatabase::class.java, "todo-database"
            ).build()
        }
    }

    private fun createNotificationChannel() {
        val name = "Todo Notifications"
        val descriptionText = "Alerts user for a pending Todo"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(Constant.NOTIFICATION_CHANNEL_NAME, name, importance).apply {
                description = descriptionText
            }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}