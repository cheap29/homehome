package com.homehome.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homehome.app.R
import com.homehome.app.data.db.entity.SourceType
import com.homehome.app.data.repository.SelectableItem
import com.homehome.app.ui.component.CharacterMessageCard
import com.homehome.app.ui.viewmodel.SelectThreeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectThreeScreen(
    viewModel: SelectThreeViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.selectableItems.collectAsState()
    val selected by viewModel.selectedItems.collectAsState()
    val showLimit by viewModel.showLimitDialog.collectAsState()
    val saved by viewModel.saved.collectAsState()

    LaunchedEffect(saved) {
        if (saved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("今日の3つを選ぶ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "もどる")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = { viewModel.save() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    enabled = selected.isNotEmpty()
                ) {
                    Text("これで決める（${selected.size}/3）")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                CharacterMessageCard(
                    imageRes = R.drawable.character_select_three,
                    message = "3つ選んだら十分えらい",
                    subMessage = "明日の自分への約束は3つだけ"
                )
            }

            // 単語帳セクション（フカツさん案：統合リスト、タグで出所を示す）
            val habitItems = items.filter { it.sourceType == SourceType.HABIT }
            val taskItems = items.filter { it.sourceType == SourceType.TASK }

            if (habitItems.isNotEmpty()) {
                item {
                    Text(
                        "毎日の単語帳",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
                items(habitItems, key = { "habit_${it.id}" }) { item ->
                    SelectableItemRow(
                        item = item,
                        isSelected = selected.any { it.id == item.id && it.sourceType == item.sourceType },
                        onToggle = { viewModel.toggleItem(item) }
                    )
                }
            }

            if (taskItems.isNotEmpty()) {
                item {
                    Text(
                        "やることBOX",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
                items(taskItems, key = { "task_${it.id}" }) { item ->
                    SelectableItemRow(
                        item = item,
                        isSelected = selected.any { it.id == item.id && it.sourceType == item.sourceType },
                        onToggle = { viewModel.toggleItem(item) }
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    if (showLimit) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLimitDialog() },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "約束は3つまでにしよう。\n明日の自分に余白を残してあげよう",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissLimitDialog() }) {
                    Text("わかった")
                }
            }
        )
    }
}

@Composable
private fun SelectableItemRow(
    item: SelectableItem,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = isSelected, onCheckedChange = { onToggle() })
            Spacer(Modifier.width(8.dp))
            Text(item.title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        }
    }
}
