package com.example.myapplication

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

class TasksViewModel(private val repository: TaskItemRepository) : ViewModel() {

    var taskItems: LiveData<List<TaskItem>> = repository.allTaskItems.asLiveData()


    fun addTaskItem(newTask :TaskItem) = viewModelScope.launch {
        repository.insertTaskItem(newTask)
    }


    fun updateTaskItem(taskItem: TaskItem) = viewModelScope.launch {
        repository.updateTaskItem(taskItem)
    }

    fun setCompleted(taskItem: TaskItem) = viewModelScope.launch {
        if(!taskItem.complete){
            taskItem.completedDate = TaskItem.dateFormatter.format(LocalDate.now(ZoneId.of("Asia/Singapore")))
            taskItem.completeTime = TaskItem.timeFormatter.format(LocalTime.now())
            taskItem.complete = true
        }
        repository.updateTaskItem(taskItem)
    }

    class TaskItemModelFactory(private val repository: TaskItemRepository) : ViewModelProvider.Factory{
        override fun <T: ViewModel> create(modelClass: Class<T>): T{
            if(modelClass.isAssignableFrom(TasksViewModel::class.java))
                return TasksViewModel(repository) as T

            throw IllegalArgumentException("Unknown Class for View Model")
        }
    }






}
