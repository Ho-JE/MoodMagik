package com.example.myapplication

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

class TasksViewModel : ViewModel() {

    var taskItems: MutableLiveData<MutableList<TaskItem>> = MutableLiveData<MutableList<TaskItem>>()

    init{
        taskItems.value = mutableListOf()
    }

    fun addTaskItem(newTask :TaskItem){
        val list = taskItems.value
        list!!.add(newTask)
        taskItems.postValue(list)
    }

    fun updateTaskItem(id: UUID, name:String, desc: String, dueTime: LocalTime?,dueDate: LocalDate){
        val list = taskItems.value
        val task = list!!.find{it.id==id}!!
        task.name = name
        task.desc = desc
        task.dueTime = dueTime
        task.dueDate= dueDate
        taskItems.postValue(list)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setComplete(id:UUID){
        val list = taskItems.value
        val task = list!!.find{it.id==id}!!
        if(task.completedDate==null){
            task.completedDate = LocalDate.now(ZoneId.of("Asia/Singapore"))
        }
        if(!task.complete){
            task.complete=true
        }
        if(task.completeTime == null){
            task.completeTime = LocalTime.now()
        }
        taskItems.postValue(list)
    }




}
