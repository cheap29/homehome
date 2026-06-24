package com.homehome.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homehome.app.data.db.entity.UserStatsEntity
import com.homehome.app.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PraiseVaultViewModel(private val repository: AppRepository) : ViewModel() {

    val stats: StateFlow<UserStatsEntity> = repository.observeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserStatsEntity())

    fun tryEnterForest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (repository.consumeSuperPraise()) onSuccess()
        }
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PraiseVaultViewModel(repository) as T
    }
}
