package com.example.myapplication.recordings

import android.app.Application

class MoodMagicApplication1: Application() {
    private val database1 by lazy { RecordingItemDatabase.getDatabase(this)}
    val repository1 by lazy { RecordingItemRepository(database1.recordingItemDao()) }
}