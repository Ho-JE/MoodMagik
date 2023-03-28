package com.example.myapplication.recordings

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class RecordingItemRepository(private val recordingItemDao: RecordingItemDao) {

    val allRecordingItems : Flow<List<RecordingItem>> = recordingItemDao.allRecordingItems()

    @WorkerThread
    suspend fun insertRecordingItem(recordingItem: RecordingItem){
        recordingItemDao.insertRecordingItem(recordingItem)
    }

    @WorkerThread
    suspend fun updateRecordingItem(recordingItem: RecordingItem){
        recordingItemDao.updateRecordingItem(recordingItem)
    }

    @WorkerThread
    suspend fun deleteRecordingItem(recordingItem: RecordingItem){
        recordingItemDao.delRecordingItem(recordingItem)
    }
}