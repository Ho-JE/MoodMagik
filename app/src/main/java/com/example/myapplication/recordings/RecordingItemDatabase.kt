package com.example.myapplication.recordings

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = (arrayOf(RecordingItem::class)), version = 1, exportSchema = false)
abstract class RecordingItemDatabase: RoomDatabase() {

    abstract fun recordingItemDao(): RecordingItemDao

    companion object{
        @Volatile
        private var INSTANCE : RecordingItemDatabase? = null

        fun getDatabase(context: Context): RecordingItemDatabase {
            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecordingItemDatabase::class.java,
                    "recording_item_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}