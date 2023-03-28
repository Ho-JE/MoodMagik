package com.example.myapplication.recordings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "recording_item_table")
class RecordingItem(

    @ColumnInfo(name="name") var name: String,
    @ColumnInfo(name="timeList") var timeList: String?,
    @ColumnInfo(name="emotionList") var emotionList: String?,
    @ColumnInfo(name="reference") var reference: String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)