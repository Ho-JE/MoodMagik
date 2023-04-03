package com.example.myapplication.tasks

import android.app.Application
import com.example.myapplication.recordings.RecordingItemDatabase
import com.example.myapplication.recordings.RecordingItemRepository
import com.example.myapplication.tasks.TaskItemDatabase
import com.example.myapplication.tasks.TaskItemRepository

class MoodMagicApplication: Application() {
    private val database by lazy { TaskItemDatabase.getDatabase(this)}
    val repository by lazy { TaskItemRepository(database.taskItemDao()) }

    private val recordingDatabase by lazy { RecordingItemDatabase.getDatabase(this) }
    val recordingRepository by lazy { RecordingItemRepository(recordingDatabase.recordingItemDao()) }
}