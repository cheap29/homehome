package com.homehome.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homehome.app.data.db.entity.DailyPlanItemEntity
import com.homehome.app.data.db.entity.ReflectionSessionEntity
import com.homehome.app.data.repository.AppRepository
import com.homehome.app.data.repository.HomeState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: AppRepository) : ViewModel() {

    val homeState: StateFlow<HomeState> = repository.observeHomeState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState(null, emptyList()))

    fun toggleCheck(item: DailyPlanItemEntity) {
        viewModelScope.launch {
            repository.togglePlanItemChecked(item.id, !item.isChecked)
        }
    }

    fun resetPlan() {
        val session = homeState.value.session ?: return
        viewModelScope.launch {
            repository.clearPlanItems(session.id)
        }
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(repository) as T
    }
}
