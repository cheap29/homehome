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

data class HistoryDetailState(
    val session: ReflectionSessionEntity? = null,
    val results: List<ReflectionResultEntity> = emptyList()
) {
    val plannedResults get() = results.filter { it.isPlanned }
    val bonusResults get() = results.filter { !it.isPlanned }
}

class HistoryDetailViewModel(
    private val repository: AppRepository,
    private val sessionId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryDetailState())
    val state: StateFlow<HistoryDetailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeHistoryDetail(sessionId).collect { results ->
                _state.value = _state.value.copy(results = results)
            }
        }
        viewModelScope.launch {
            repository.observeHistory().collect { sessions ->
                val session = sessions.firstOrNull { it.id == sessionId }
                _state.value = _state.value.copy(session = session)
            }
        }
    }

    class Factory(private val repository: AppRepository, private val sessionId: Long) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HistoryDetailViewModel(repository, sessionId) as T
    }
}
