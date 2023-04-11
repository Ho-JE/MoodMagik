package com.example.myapplication.tasks

import android.app.Application

class MoodMagicApplication: Application() {
    private val database by lazy { TaskItemDatabase.getDatabase(this)}
    val repository by lazy { TaskItemRepository(database.taskItemDao()) }
}