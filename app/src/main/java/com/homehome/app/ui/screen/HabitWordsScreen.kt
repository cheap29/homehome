package com.homehome.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homehome.app.data.db.entity.HabitWordEntity
import com.homehome.app.ui.viewmodel.HabitWordsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitWordsScreen(
    viewModel: HabitWordsViewModel,
    onBack: () -> Unit
) {
    val words by viewModel.habitWords.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingWord by remember { mutableStateOf<HabitWordEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("毎日の単語帳") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "もどる")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "追加")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(words, key = { it.id }) { word ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            word.title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { editingWord = word }) {
                            Icon(Icons.Default.Edit, "編集", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { viewModel.deleteWord(word) }) {
                            Icon(Icons.Default.Delete, "削除", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        WordEditDialog(
            initial = "",
            onConfirm = { title ->
                viewModel.addWord(title)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    editingWord?.let { word ->
        WordEditDialog(
            initial = word.title,
            onConfirm = { title ->
                viewModel.updateWord(word, title)
                editingWord = null
            },
            onDismiss = { editingWord = null }
        )
    }
}

@Composable
private fun WordEditDialog(
    initial: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial.isEmpty()) "単語を追加" else "単語を編集") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("単語") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (text.isNotBlank()) onConfirm(text) },
                enabled = text.isNotBlank()
            ) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("やめる") }
        }
    )
}
