package com.homehome.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homehome.app.data.db.entity.TaskEntity
import com.homehome.app.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskBoxViewModel(private val repository: AppRepository) : ViewModel() {

    val tasks: StateFlow<List<TaskEntity>> = repository.observeActiveTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTask(title: String, memo: String?) {
        viewModelScope.launch { repository.addTask(title, memo.takeIf { !it.isNullOrBlank() }) }
    }

    fun updateTask(task: TaskEntity, newTitle: String, newMemo: String?) {
        viewModelScope.launch {
            repository.updateTask(task.copy(title = newTitle, memo = newMemo.takeIf { !it.isNullOrBlank() }))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            TaskBoxViewModel(repository) as T
    }
}
