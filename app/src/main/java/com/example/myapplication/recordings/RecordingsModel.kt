package com.example.myapplication.recordings

import androidx.lifecycle.*
import com.example.myapplication.tasks.TaskItemRepository
import kotlinx.coroutines.launch

class RecordingViewModel(private val repository: RecordingItemRepository) : ViewModel() {

    var recordingItems: LiveData<List<RecordingItem>> = repository.allRecordingItems.asLiveData()


    fun addRecordingItem(newRecording : RecordingItem) = viewModelScope.launch {
        repository.insertRecordingItem(newRecording)
    }


    fun updateRecordingItem(recordingItem: RecordingItem) = viewModelScope.launch {
        repository.updateRecordingItem(recordingItem)
    }

    fun deleteRecordingItem(recordingItem: RecordingItem) = viewModelScope.launch {
        repository.deleteRecordingItem(recordingItem)
    }
    class RecordingItemModelFactory(private val repository: RecordingItemRepository) : ViewModelProvider.Factory{
        override fun <T: ViewModel> create(modelClass: Class<T>): T{
            if(modelClass.isAssignableFrom(RecordingViewModel::class.java))
                return RecordingViewModel(repository) as T

            throw IllegalArgumentException("Unknown Class for View Model")
        }
    }




}
