package com.homehome.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homehome.app.data.db.entity.DailyPlanItemEntity
import com.homehome.app.data.repository.AppRepository
import com.homehome.app.data.repository.SelectableItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SelectThreeViewModel(private val repository: AppRepository) : ViewModel() {

    val selectableItems: StateFlow<List<SelectableItem>> = repository.observeSelectableItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedItems = MutableStateFlow<List<SelectableItem>>(emptyList())
    val selectedItems: StateFlow<List<SelectableItem>> = _selectedItems.asStateFlow()

    private val _showLimitDialog = MutableStateFlow(false)
    val showLimitDialog: StateFlow<Boolean> = _showLimitDialog.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    fun toggleItem(item: SelectableItem) {
        val current = _selectedItems.value
        if (current.any { it.id == item.id && it.sourceType == item.sourceType }) {
            _selectedItems.value = current.filter { !(it.id == item.id && it.sourceType == item.sourceType) }
        } else {
            if (current.size >= 3) {
                _showLimitDialog.value = true
            } else {
                _selectedItems.value = current + item
            }
        }
    }

    fun dismissLimitDialog() {
        _showLimitDialog.value = false
    }

    fun save() {
        viewModelScope.launch {
            val session = repository.getOrCreateOpenSession()
            val items = _selectedItems.value.mapIndexed { index, item ->
                DailyPlanItemEntity(
                    sessionId = session.id,
                    sourceType = item.sourceType,
                    sourceId = item.sourceId,
                    titleSnapshot = item.title,
                    orderIndex = index
                )
            }
            repository.savePlanItems(session.id, items)
            _saved.value = true
        }
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SelectThreeViewModel(repository) as T
    }
}
