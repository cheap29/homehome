package com.homehome.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homehome.app.data.db.entity.ReflectionResultEntity
import com.homehome.app.data.db.entity.ReflectionSessionEntity
import com.homehome.app.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReflectionCompleteState(
    val session: ReflectionSessionEntity? = null,
    val results: List<ReflectionResultEntity> = emptyList()
) {
    val plannedResults get() = results.filter { it.isPlanned }
    val bonusResults get() = results.filter { !it.isPlanned }
    val checkedCount get() = plannedResults.count { it.isCompleted }
    val bonusCount get() = bonusResults.size
    val totalCount get() = checkedCount + bonusCount
    val praiseText get() = session?.praiseText ?: ""
}

class ReflectionCompleteViewModel(
    private val repository: AppRepository,
    private val sessionId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(ReflectionCompleteState())
    val state: StateFlow<ReflectionCompleteState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeHistoryDetail(sessionId).collect { results ->
                _state.value = _state.value.copy(results = results)
            }
        }
        viewModelScope.launch {
            repository.observeSessionById(sessionId).collect { session ->
                _state.value = _state.value.copy(session = session)
            }
        }
    }

    class Factory(private val repository: AppRepository, private val sessionId: Long) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ReflectionCompleteViewModel(repository, sessionId) as T
    }
}
