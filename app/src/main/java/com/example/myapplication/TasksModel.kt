package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TasksViewModel : ViewModel() {
    val tasks = MutableLiveData<ArrayList<Tasks>>()

    fun addTask(task: Tasks) {
        val taskList = tasks.value ?: ArrayList()
        taskList.add(task)
        tasks.value = taskList
    }


}
