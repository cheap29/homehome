package com.homehome.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homehome.app.data.db.entity.DailyPlanItemEntity
import com.homehome.app.data.db.entity.TaskEntity
import com.homehome.app.data.repository.AppRepository
import com.homehome.app.data.repository.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReflectionViewModel(private val repository: AppRepository) : ViewModel() {

    val homeState: StateFlow<HomeState> = repository.observeHomeState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState(null, emptyList()))

    val activeTasks: StateFlow<List<TaskEntity>> = repository.observeActiveTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showBonusSheet = MutableStateFlow(false)
    val showBonusSheet: StateFlow<Boolean> = _showBonusSheet.asStateFlow()

    private val _completedSessionId = MutableStateFlow<Long?>(null)
    val completedSessionId: StateFlow<Long?> = _completedSessionId.asStateFlow()

    fun toggleCheck(item: DailyPlanItemEntity) {
        viewModelScope.launch {
            repository.togglePlanItemChecked(item.id, !item.isChecked)
        }
    }

    fun openBonusSheet() { _showBonusSheet.value = true }
    fun closeBonusSheet() { _showBonusSheet.value = false }

    fun addBonusFromTask(task: TaskEntity) {
        val session = homeState.value.session ?: return
        viewModelScope.launch {
            repository.addBonusResult(session.id, task.title, "TASK", task.id)
            _showBonusSheet.value = false
        }
    }

    fun addBonusFree(title: String) {
        val session = homeState.value.session ?: return
        viewModelScope.launch {
            repository.addBonusResult(session.id, title, "FREE", null)
            _showBonusSheet.value = false
        }
    }

    fun completeReflection() {
        val state = homeState.value
        val session = state.session ?: return
        viewModelScope.launch {
            val checked = state.planItems.filter { it.isChecked }
            repository.completeReflection(session.id, checked, state.planItems)
            _completedSessionId.value = session.id
        }
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ReflectionViewModel(repository) as T
    }
}
