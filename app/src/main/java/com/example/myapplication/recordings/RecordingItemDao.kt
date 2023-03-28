package com.example.myapplication.recordings

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingItemDao {
    @Query("SELECT * FROM recording_item_table ORDER BY id ASC")
    fun allRecordingItems(): Flow<List<RecordingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecordingItem(recordingItem: RecordingItem)

    @Update
    suspend fun updateRecordingItem(recordingItem: RecordingItem)

    @Delete
    suspend fun delRecordingItem(recordingItem: RecordingItem)
}