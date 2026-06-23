package com.homehome.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homehome.app.data.db.entity.HabitWordEntity
import com.homehome.app.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitWordsViewModel(private val repository: AppRepository) : ViewModel() {

    val habitWords: StateFlow<List<HabitWordEntity>> = repository.observeHabitWords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addWord(title: String) {
        viewModelScope.launch { repository.addHabitWord(title) }
    }

    fun updateWord(word: HabitWordEntity, newTitle: String) {
        viewModelScope.launch { repository.updateHabitWord(word.copy(title = newTitle)) }
    }

    fun deleteWord(word: HabitWordEntity) {
        viewModelScope.launch { repository.deleteHabitWord(word) }
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HabitWordsViewModel(repository) as T
    }
}
