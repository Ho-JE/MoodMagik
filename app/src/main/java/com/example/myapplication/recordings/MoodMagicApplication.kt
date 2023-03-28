package com.example.myapplication.recordings

import android.app.Application

class MoodMagicApplication: Application() {
    private val database by lazy { RecordingItemDatabase.getDatabase(this)}
    val repository by lazy { RecordingItemRepository(database.recordingItemDao()) }
}