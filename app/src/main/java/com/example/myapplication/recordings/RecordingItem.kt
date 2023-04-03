package com.example.myapplication.recordings

import androidx.room.*


@Entity(tableName = "recording_item_table")
class RecordingItem(

    @ColumnInfo(name="name") var name: String,
    @ColumnInfo(name="timeList") var timeList: String,
    @ColumnInfo(name="emotionList") var emotionList: String,
//    @ColumnInfo(name="reference") var reference: String,
    @ColumnInfo(name="Date") var Date: String,
    @ColumnInfo(name="Duration") var Duration: String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

) {
    companion object {
        fun fromArrayList(list: ArrayList<String>): String {
            return list.joinToString(",")
        }
        fun toArrayList(string: String): ArrayList<String> {
            return string.split(",").toCollection(ArrayList())
        }
    }
}
