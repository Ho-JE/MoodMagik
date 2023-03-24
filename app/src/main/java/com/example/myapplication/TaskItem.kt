package com.example.myapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Entity(tableName = "task_item_table")
class TaskItem(

    @ColumnInfo(name="name") var name: String,
    @ColumnInfo(name="desc") var desc: String,
    @ColumnInfo(name="dueTimeStr") var dueTime: String?,
    @ColumnInfo(name="dueDateStr") var dueDate: String?,
    @ColumnInfo(name="completedDateStr") var completedDate: String?,
    @ColumnInfo(name="completedTimeStr") var completeTime:String?,
    @ColumnInfo(name="complete") var complete: Boolean,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)

{
    fun dueTime():LocalTime? = if (dueTime==null) null
        else{LocalTime.parse(dueTime, timeFormatter)}

    fun dueDate(): LocalDate? = if(dueDate == null) null else {
        LocalDate.parse(dueDate, dateFormatter)
    }
    fun completeTime():LocalTime? = if (completeTime==null) null else
        {LocalTime.parse(completeTime, timeFormatter)}

    fun completedDate(): LocalDate? = if(completedDate == null) null else {
        LocalDate.parse(completedDate, dateFormatter)
    }

    companion object{
        val timeFormatter : DateTimeFormatter = DateTimeFormatter.ISO_TIME
        val dateFormatter : DateTimeFormatter = DateTimeFormatter.ISO_DATE
    }

}