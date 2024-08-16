package com.ristu.todoapp

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ristu.todoapp.manager.TodoAlarmReceiver
import com.ristu.todoapp.model.TodoModel

class TodoManagerImpl(private val context: Context) : TodoManager {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("MissingPermission", "ScheduleExactAlarm")
    override fun scheduleTodo(item: TodoModel) {
        val intent = Intent(context, TodoAlarmReceiver::class.java)
        intent.putExtra("TITLE", item.title)
        intent.putExtra("BODY", item.body)
        intent.putExtra("UID", item.uid)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            Helper.convertToTimestamp(item.date!!, item.time!!),
            PendingIntent.getBroadcast(
                context,
                item.uid.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancelTodo(uid: Long) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                uid.toInt(),
                Intent(context, TodoAlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}